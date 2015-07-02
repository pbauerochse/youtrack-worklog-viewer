package de.pbauerochse.youtrack.connector;

import de.pbauerochse.youtrack.connector.createreport.request.CreateReportRequestEntity;
import de.pbauerochse.youtrack.connector.createreport.response.ReportDetailsResponse;
import de.pbauerochse.youtrack.csv.YouTrackCsvReportProcessor;
import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.util.ExceptionUtil;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.JacksonUtil;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class YouTrackConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTrackConnector.class);

    private static final int MAX_REPORT_STATUS_POLLS = 5;

    private static YouTrackConnector instance;

    private CloseableHttpClient client;

    public static YouTrackConnector getInstance() {
        if (instance == null) {
            instance = new YouTrackConnector();
        }
        return instance;
    }

    private YouTrackConnector() {
        initClient();
    }

    private void initClient() {
        if (client == null) {
            LOGGER.debug("Initializing HttpClient");
            List<Header> headerList = new ArrayList<>();
            headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36"));
            headerList.add(new BasicHeader("Accept-Encoding", "gzip, deflate, sdch"));
            headerList.add(new BasicHeader("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4"));

            RequestConfig config = RequestConfig
                    .custom()
                    .setConnectTimeout(10 * 1000)                // 10s
                    .setConnectionRequestTimeout(10 * 1000)      // 10s
                    .build();

            client = HttpClients
                    .custom()
                    .setDefaultHeaders(headerList)
                    .setDefaultRequestConfig(config)
                    .build();
        }
    }

    public Task<WorklogResult> getWorklogTaskForUser(String youtrackUrl, String username, String password, ReportTimerange reportTimerange) {
        if (StringUtils.isBlank(youtrackUrl)) throw ExceptionUtil.getIllegalArgumentException("exceptions.main.worker.youtrackurl");
        if (StringUtils.isBlank(username)) throw ExceptionUtil.getIllegalArgumentException("exceptions.main.worker.youtrackuser");
        if (StringUtils.isBlank(password)) throw ExceptionUtil.getIllegalArgumentException("exceptions.main.worker.youtrackpassword");
        if (reportTimerange == null) throw ExceptionUtil.getIllegalArgumentException("exceptions.main.worker.reportingrange");

        LOGGER.debug("Creating WorklogTask for user {} and timerange {}", username, reportTimerange);
        return new Task<WorklogResult>() {
            @Override
            protected WorklogResult call() throws Exception {
                updateProgress(0, 100);
                updateMessage(FormattingUtil.getFormatted("worker.progress.login", username));

                // login to the youtrack api
                LOGGER.debug("Logging in to YouTrack at {} as {}", youtrackUrl, username);
                login(youtrackUrl, username, password);
                updateProgress(10, 100);

                // create report
                updateMessage(FormattingUtil.getFormatted("worker.progress.creatingreport", FormattingUtil.getFormatted(reportTimerange.getLabelKey())));
                ReportDetailsResponse reportDetailsResponse = createReport(youtrackUrl, reportTimerange);
                updateProgress(50, 100);

                // report generation succeeded and is in progress right now
                // giant try block to finally delete the report again even
                // in error cases to prevent polluted user report view
                try {
                    updateMessage(FormattingUtil.getFormatted("worker.progress.waitingforrecalculation"));

                    // poll report status every second
                    // until report generation is finished or MAX_REPORT_STATUS_POLLS reached
                    int currentRetry = 0;
                    while (!StringUtils.equals(ReportDetailsResponse.READY_STATE, reportDetailsResponse.getState()) && currentRetry++ < MAX_REPORT_STATUS_POLLS) {
                        Thread.sleep(1000);
                        reportDetailsResponse = getReportDetails(youtrackUrl, reportDetailsResponse.getId());
                    }

                    if (!StringUtils.equals(ReportDetailsResponse.READY_STATE, reportDetailsResponse.getState())) {
                        throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.recalculation", MAX_REPORT_STATUS_POLLS);
                    }

                    // download the generated report
                    updateMessage(FormattingUtil.getFormatted("worker.progress.downloadingreport", reportDetailsResponse.getId()));

                    String downloadReportUrlTemplate = buildYoutrackApiUrl(youtrackUrl, "current/reports/%s/export");
                    HttpGet request = new HttpGet(String.format(downloadReportUrlTemplate, reportDetailsResponse.getId()));

                    WorklogResult worklogResult = client.execute(request, response -> {
                        WorklogResult result = new WorklogResult(username);

                        HttpEntity entity = response.getEntity();
                        ByteArrayInputStream reportDataInputStream = null;

                        try {
                            byte[] reportBytes = EntityUtils.toByteArray(entity);
                            reportDataInputStream = new ByteArrayInputStream(reportBytes);
                        } finally {
                            ((CloseableHttpResponse) response).close();
                        }

                        if (!isValidResponseCode(response.getStatusLine())) {
                            // invalid response code
                            int statusCode = response.getStatusLine().getStatusCode();
                            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.statuscode",response.getStatusLine().getReasonPhrase(), statusCode);
                        }

                        // success
                        updateProgress(80, 100);
                        updateMessage(FormattingUtil.getFormatted("worker.progress.processingreport"));
                        YouTrackCsvReportProcessor.processResponse(reportDataInputStream, result);

                        return result;
                    });

                    updateMessage(FormattingUtil.getFormatted("worker.progress.done"));
                    updateProgress(100, 100);

                    return worklogResult;
                } finally {
                    // delete the report again
                    deleteReport(youtrackUrl, reportDetailsResponse.getId());
                }
            }
        };
    }

    private void login(String youtrackUrl, String username, String password) throws Exception {
        String loginUrl = buildYoutrackApiUrl(youtrackUrl, "user/login");

        HttpPost request = new HttpPost(loginUrl);

        List<NameValuePair> requestParameters = new ArrayList<>();
        requestParameters.add(new BasicNameValuePair("login", username));
        requestParameters.add(new BasicNameValuePair("password", password));
        request.setEntity(new UrlEncodedFormEntity(requestParameters, "utf-8"));

        CloseableHttpResponse response = client.execute(request);

        try {
            EntityUtils.consumeQuietly(response.getEntity());
        } finally {
            response.close();
        }

        if (!isValidResponseCode(response.getStatusLine())) {
            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.login", response.getStatusLine().getReasonPhrase(), response.getStatusLine().getStatusCode());
        }
    }

    /**
     * Creates the temporary worklog report using the given url and timerange
     * @param youtrackUrl The target url of the youtrack api
     * @param timerange The timerange to generate the report for
     * @return
     * @throws Exception
     */
    private ReportDetailsResponse createReport(String youtrackUrl, ReportTimerange timerange) throws Exception {
        LOGGER.debug("Creating temporary timereport");
        String createReportUrl = buildYoutrackApiUrl(youtrackUrl, "current/reports");

        HttpPost createReportRequest = new HttpPost(createReportUrl);

        // request body
        CreateReportRequestEntity requestEntity = new CreateReportRequestEntity(timerange);
        String requestEntityAsString = JacksonUtil.writeObject(requestEntity);

        createReportRequest.setEntity(new StringEntity(requestEntityAsString, "utf-8"));
        createReportRequest.addHeader("Content-Type", "application/json;charset=UTF-8");

        // create report

        try (CloseableHttpResponse response = client.execute(createReportRequest)) {
            if (!isValidResponseCode(response.getStatusLine())) {
                LOGGER.error("Creating temporary timereport failed: {}", response.getStatusLine().getReasonPhrase());
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.creatingreport", response.getStatusLine().getReasonPhrase(), response.getStatusLine().getStatusCode());
            }

            String responseJson = EntityUtils.toString(response.getEntity());

            if (StringUtils.isBlank(responseJson)) {
                LOGGER.warn("Response from youtrack was blank");
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.blankresponse");
            }

            return JacksonUtil.parseValue(new StringReader(responseJson), ReportDetailsResponse.class);
        }
    }

    private ReportDetailsResponse getReportDetails(String youtrackUrl, String reportId) throws Exception {
        String reportUrlTemplate = buildYoutrackApiUrl(youtrackUrl, "current/reports/%s");
        LOGGER.debug("Fetching report details from {}", reportUrlTemplate);

        HttpGet reportDetailsRequest = new HttpGet(String.format(reportUrlTemplate, reportId));

        try (CloseableHttpResponse httpResponse = client.execute(reportDetailsRequest)) {
            if (!isValidResponseCode(httpResponse.getStatusLine())) {
                LOGGER.warn("Fetching report details from {} failed: {}", reportUrlTemplate, httpResponse.getStatusLine().getReasonPhrase());
                EntityUtils.consumeQuietly(httpResponse.getEntity());
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.reportstatus", httpResponse.getStatusLine().getReasonPhrase(), httpResponse.getStatusLine().getStatusCode());
            }

            String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
            LOGGER.debug("Received JSON response {}", jsonResponse);
            return JacksonUtil.parseValue(new StringReader(jsonResponse), ReportDetailsResponse.class);
        }
    }

    private void deleteReport(String youtrackUrl, String reportId) throws IOException {
        String reportUrlTemplate = buildYoutrackApiUrl(youtrackUrl, "current/reports/%s");
        LOGGER.debug("Deleting temporary report using url {}", reportUrlTemplate);

        HttpDelete deleteRequest = new HttpDelete(String.format(reportUrlTemplate, reportId));

        try (CloseableHttpResponse response = client.execute(deleteRequest)) {
            EntityUtils.consumeQuietly(response.getEntity());

            if (!isValidResponseCode(response.getStatusLine())) {
                LOGGER.warn("Could not delete temporary report using url {}: {}", reportUrlTemplate, response.getStatusLine().getReasonPhrase());
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.deletereport", response.getStatusLine().getReasonPhrase(), response.getStatusLine().getStatusCode());
            }
        }
    }

    private static String buildYoutrackApiUrl(String userDefinedUrl, String path) {
        StringBuilder finalUrl = new StringBuilder(StringUtils.trim(userDefinedUrl));

        if (!StringUtils.endsWith(userDefinedUrl, "/") && !StringUtils.startsWith(path, "/")) {
            finalUrl.append('/');
        }

        if (!StringUtils.endsWith(finalUrl, "rest/")) {
            finalUrl.append("rest/");
        }

        return finalUrl.append(path).toString();
    }

    private static boolean isValidResponseCode(StatusLine statusLine) {
        if (statusLine == null) throw ExceptionUtil.getIllegalArgumentException("exceptions.main.worker.nullstatus");
        int statusCode = statusLine.getStatusCode();

        return (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES);
    }
}
