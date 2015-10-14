package de.pbauerochse.worklogviewer.youtrack.connector;

import com.fasterxml.jackson.core.type.TypeReference;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.JacksonUtil;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.createreport.request.CreateReportRequestEntity;
import de.pbauerochse.worklogviewer.youtrack.createreport.response.ReportDetailsResponse;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 14.10.15
 */
class ApiLoginConnector implements YouTrackConnector {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private CloseableHttpClient client;

    private Integer connectionParameterHashCode;

    ApiLoginConnector() {
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

    protected void validateLoggedInState() throws Exception {
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        if (connectionParameterHashCode == null || !connectionParameterHashCode.equals(settings.getConnectionParametersHashCode())) {
            LOGGER.debug("Login parameters not loaded yet or changed. Performing login");

            String loginUrl = buildYoutrackApiUrl("user/login");

            HttpPost request = new HttpPost(loginUrl);

            List<NameValuePair> requestParameters = new ArrayList<>();
            requestParameters.add(new BasicNameValuePair("login", settings.getYoutrackUsername()));
            requestParameters.add(new BasicNameValuePair("password", settings.getYoutrackPassword()));
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

            // connection successfull with these settings
            // hence store them
            connectionParameterHashCode = settings.getConnectionParametersHashCode();
        }
    }

    @Override
    public List<GroupByCategory> getPossibleGroupByCategories() throws Exception {

        validateLoggedInState();

        String getGroupByCategoriesUrl = buildYoutrackApiUrl("reports/timeReports/possibleGroupByCategories");

        HttpGet request = new HttpGet(getGroupByCategoriesUrl);
        request.addHeader("Accept", "application/json, text/plain, */*");

        try (CloseableHttpResponse httpResponse = client.execute(request)) {
            if (!isValidResponseCode(httpResponse.getStatusLine())) {
                LOGGER.warn("Fetching groupBy categories from {} failed: {}", getGroupByCategoriesUrl, httpResponse.getStatusLine().getReasonPhrase());
                EntityUtils.consumeQuietly(httpResponse.getEntity());
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.groupbycategories", httpResponse.getStatusLine().getReasonPhrase(), httpResponse.getStatusLine().getStatusCode());
            }

            String jsonResponse = EntityUtils.toString(httpResponse.getEntity());
            LOGGER.debug("Received JSON groupByCategories response {}", jsonResponse);

            return JacksonUtil.parseValue(new StringReader(jsonResponse), new TypeReference<List<GroupByCategory>>() {});
        }
    }

    /**
     * Creates the temporary worklog report using the given url and timerange
     *
     * @param requestEntity The request entity for the report
     * @return
     * @throws Exception
     */
    @Override
    public ReportDetailsResponse createReport(CreateReportRequestEntity requestEntity) throws Exception {

        validateLoggedInState();

        LOGGER.debug("Creating temporary timereport");
        String createReportUrl = buildYoutrackApiUrl("current/reports");

        HttpPost createReportRequest = new HttpPost(createReportUrl);

        // request body
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

    @Override
    public ReportDetailsResponse getReportDetails(String reportId) throws Exception {

        validateLoggedInState();

        String reportUrlTemplate = buildYoutrackApiUrl("current/reports/%s");
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

    @Override
    public void deleteReport(String reportId) throws Exception {

        validateLoggedInState();

        String reportUrlTemplate = buildYoutrackApiUrl("current/reports/%s");
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

    @Override
    public ByteArrayInputStream downloadReport(String reportId) throws Exception {

        validateLoggedInState();

        String downloadReportUrlTemplate = buildYoutrackApiUrl("current/reports/%s/export");
        HttpGet request = new HttpGet(String.format(downloadReportUrlTemplate, reportId));
        return client.execute(request, response -> {
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
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.statuscode", response.getStatusLine().getReasonPhrase(), statusCode);
            }

            return reportDataInputStream;
        });
    }

    private String buildYoutrackApiUrl(String path) {
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();
        StringBuilder finalUrl = new StringBuilder(StringUtils.trim(settings.getYoutrackUrl()));

        if (!StringUtils.endsWith(settings.getYoutrackUrl(), "/") && !StringUtils.startsWith(path, "/")) {
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

    @Override
    public YouTrackAuthenticationMethod getAuthenticationMethod() {
        return YouTrackAuthenticationMethod.HTTP_API;
    }
}
