package de.pbauerochse.worklogviewer.youtrack.v20174;

import com.fasterxml.jackson.core.type.TypeReference;
import de.pbauerochse.worklogviewer.util.DateUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.JacksonUtil;
import de.pbauerochse.worklogviewer.youtrack.*;
import de.pbauerochse.worklogviewer.youtrack.csv.CsvReportData;
import de.pbauerochse.worklogviewer.youtrack.csv.CsvReportReader;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.Issue;
import de.pbauerochse.worklogviewer.youtrack.issuedetails.IssueDetails;
import de.pbauerochse.worklogviewer.youtrack.issuedetails.IssueDetailsResponse;
import de.pbauerochse.worklogviewer.youtrack.v20174.types.FieldBasedGrouping;
import de.pbauerochse.worklogviewer.youtrack.v20174.types.GroupByTypes;
import de.pbauerochse.worklogviewer.youtrack.v20174.types.GroupingField;
import de.pbauerochse.worklogviewer.youtrack.v20174.types.WorkItemBasedGrouping;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;
import static de.pbauerochse.worklogviewer.util.HttpClientUtil.getHttpClient;
import static de.pbauerochse.worklogviewer.util.HttpClientUtil.isValidResponseCode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class YouTrackServiceV20174 implements YouTrackService {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTrackServiceV20174.class);

    /**
     * The maximum amount of polls to wait for the report generation
     */
    private static final int MAX_POLL_COUNT = 10;

    /**
     * The interval in seconds between the polls
     * for the report generation to finish
     */
    private static final int POLL_INTERVAL_IN_SECONDS = 2;

    /**
     * GroupBy Criteria, that can not be retrieved with the {@link #getPossibleGroupByCategories()}
     * call but are always available to be used
     */
    private static final List<GroupByCategory> CONSTANT_GROUP_BY_CRITERIA = new ArrayList<>();

    static {
        CONSTANT_GROUP_BY_CRITERIA.add(new WorkItemBasedGrouping(new GroupByTypes("WORK_TYPE", getFormatted("grouping.worktype"))));
        CONSTANT_GROUP_BY_CRITERIA.add(new WorkItemBasedGrouping(new GroupByTypes("WORK_AUTHOR", getFormatted("grouping.author"))));
        CONSTANT_GROUP_BY_CRITERIA.add(new WorkItemBasedGrouping(new GroupByTypes("WORK_AUTHOR_AND_DATE", getFormatted("grouping.authoranddate"))));
    }

    private final YouTrackUrlBuilder urlBuilder;

    public YouTrackServiceV20174(@NotNull YouTrackUrlBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    @Override
    public List<GroupByCategory> getPossibleGroupByCategories() {
        String url = urlBuilder.getGroupByCriteriaUrl();
        LOGGER.debug("Getting GroupBy Criteria using url {}", url);

        HttpGet request = new HttpGet(url);
        request.addHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*"));

        try (CloseableHttpClient client = getHttpClient(urlBuilder)) {
            try (CloseableHttpResponse response = client.execute(request)) {
                StatusLine statusLine = response.getStatusLine();

                if (!isValidResponseCode(statusLine)) {
                    LOGGER.warn("Fetching groupBy categories from {} failed: {}", url, statusLine.getReasonPhrase());
                    EntityUtils.consumeQuietly(response.getEntity());
                    throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.groupbycategories", statusLine.getReasonPhrase(), statusLine.getStatusCode());
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                LOGGER.debug("Received JSON groupByCategories response {}", jsonResponse);

                List<GroupingField> fields = JacksonUtil.parseValue(new StringReader(jsonResponse), new TypeReference<List<GroupingField>>() {
                });

                List<FieldBasedGrouping> fieldBasedGroupings = fields.stream()
                        .map(FieldBasedGrouping::new)
                        .collect(Collectors.toList());

                ArrayList<GroupByCategory> groupByCategories = new ArrayList<>();
                groupByCategories.addAll(CONSTANT_GROUP_BY_CRITERIA);
                groupByCategories.addAll(fieldBasedGroupings);
                return groupByCategories;
            }
        } catch (IOException e) {
            LOGGER.error("Could not get GroupByCriterias from {}", url, e);
            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.unknown");
        }
    }

    @Override
    public TimeReport getReport(TimeReportParameters parameters, ProgressCallback progressCallback) {
        ByteArrayInputStream reportCsvContent = getReportCsvContents(parameters, progressCallback);

        progressCallback.setProgress(getFormatted("worker.progress.processingreport"), 80);
        CsvReportData csvReportData = CsvReportReader.processResponse(reportCsvContent);
        applyResolutionDate(csvReportData);

        progressCallback.setProgress(getFormatted("worker.progress.done"), 100);
        return new TimeReport(parameters, csvReportData);
    }

    private ByteArrayInputStream getReportCsvContents(TimeReportParameters parameters, ProgressCallback progressCallback) {
        String timerangeDisplayLabel = getFormatted(parameters.getTimerangeProvider().getReportTimerange().getLabelKey());
        progressCallback.setProgress(getFormatted("worker.progress.creatingreport", timerangeDisplayLabel), 0);
        ReportDetails reportDetails = triggerReportGeneration(parameters);

        int pollCount = 0;
        progressCallback.setProgress(getFormatted("worker.progress.waitingforrecalculation"), 30);
        while (!reportDetails.isReady() && pollCount++ < MAX_POLL_COUNT) {
            waitUntilNextPoll();
            reportDetails = getReportDetails(reportDetails.getReportId());
        }

        if (!reportDetails.isReady()) {
            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.recalculation", MAX_POLL_COUNT);
        }

        progressCallback.setProgress(getFormatted("worker.progress.downloadingreport", reportDetails.getReportId()), 60);
        ByteArrayInputStream byteArrayInputStream = downloadReport(reportDetails.getReportId());

        progressCallback.setProgress(getFormatted("worker.progress.deletingreport"), 80);
        deleteReport(reportDetails.getReportId());

        return byteArrayInputStream;
    }

    private void waitUntilNextPoll() {
        try {
            Thread.sleep(POLL_INTERVAL_IN_SECONDS * 1000);
        } catch (InterruptedException e) {
            throw ExceptionUtil.getIllegalStateException("exceptions.internal", e);
        }
    }

    private ReportDetails triggerReportGeneration(@NotNull TimeReportParameters parameters) {
        String url = urlBuilder.getCreateReportUrl();
        LOGGER.debug("Creating temporary timereport using url {}", url);

        CreateReportParameters payload = new CreateReportParameters(parameters);

        // request body
        HttpPost request = new HttpPost(url);
        String payloadString = JacksonUtil.writeObject(payload);
        request.setEntity(new StringEntity(payloadString, UTF_8));
        request.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8"));

        try (CloseableHttpClient httpClient = getHttpClient(urlBuilder)) {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                StatusLine statusLine = response.getStatusLine();

                if (!isValidResponseCode(statusLine)) {
                    LOGGER.error("Creating temporary timereport failed: {}", statusLine.getReasonPhrase());
                    throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.creatingreport", statusLine.getReasonPhrase(), statusLine.getStatusCode());
                }

                String responseJson = EntityUtils.toString(response.getEntity());

                if (isBlank(responseJson)) {
                    LOGGER.warn("Response from youtrack was blank");
                    throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.blankresponse");
                }

                return JacksonUtil.parseValue(new StringReader(responseJson), ReportDetailsResponse.class);
            }

        } catch (IOException e) {
            LOGGER.error("Could not create Report from URL {}", url, e);
            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.unknown");
        }
    }

    private ReportDetails getReportDetails(String reportId) {
        String url = urlBuilder.getReportDetailsUrl(reportId);
        LOGGER.debug("Fetching report details for report {} using url {}", reportId, url);

        HttpGet request = new HttpGet(url);

        try (CloseableHttpClient httpClient = getHttpClient(urlBuilder)) {

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                StatusLine statusLine = response.getStatusLine();

                if (!isValidResponseCode(statusLine)) {
                    LOGGER.warn("Fetching report details from {} failed: {}", url, statusLine.getReasonPhrase());
                    EntityUtils.consumeQuietly(response.getEntity());
                    throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.reportstatus", statusLine.getReasonPhrase(), statusLine.getStatusCode());
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                LOGGER.debug("Received JSON response {}", jsonResponse);
                ReportStatus reportStatus = JacksonUtil.parseValue(new StringReader(jsonResponse), ReportStatus.class);
                return new ReportDetailsResponse(reportId, reportStatus);
            }

        } catch (IOException e) {
            LOGGER.error("Could not get report details from URL {}", url, e);
            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.unknown");
        }
    }

    private ByteArrayInputStream downloadReport(String reportId) {
        String url = urlBuilder.getDownloadReportUrl(reportId);
        LOGGER.debug("Downloading report {} using url {}", reportId, url);

        HttpGet request = new HttpGet(url);
        request.setConfig(RequestConfig.custom().setContentCompressionEnabled(false).build());

        try (CloseableHttpClient httpClient = getHttpClient(urlBuilder)) {
            return httpClient.execute(request, response -> {
                ByteArrayInputStream inputStream;

                try (CloseableHttpResponse closeableHttpResponse = (CloseableHttpResponse) response) {
                    HttpEntity entity = closeableHttpResponse.getEntity();
                    byte[] bytes = EntityUtils.toByteArray(entity);
                    inputStream = new ByteArrayInputStream(bytes);
                }

                StatusLine statusLine = response.getStatusLine();
                if (seemsToBeBlankReport(statusLine)) {
                    LOGGER.warn("Got {} - {} from YouTrack, report propably did not contain any data", statusLine.getStatusCode(), statusLine.getReasonPhrase());
                    return null;
                }

                if (!isValidResponseCode(response.getStatusLine())) {
                    // invalid response code
                    int statusCode = response.getStatusLine().getStatusCode();
                    throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.statuscode", response.getStatusLine().getReasonPhrase(), statusCode);
                }

                return inputStream;
            });
        } catch (IOException e) {
            LOGGER.error("Could not download report from URL {}", url, e);
            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.unknown");
        }
    }

    private void deleteReport(String reportId) {
        String url = urlBuilder.getDeleteReportUrl(reportId);
        LOGGER.debug("Deleting report {} using url {}", reportId, url);

        HttpDelete request = new HttpDelete(url);

        try (CloseableHttpClient httpClient = getHttpClient(urlBuilder)) {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                EntityUtils.consumeQuietly(response.getEntity());

                if (!isValidResponseCode(response.getStatusLine())) {
                    LOGGER.warn("Could not delete temporary report using url {}: {}", url, response.getStatusLine().getReasonPhrase());
                    throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.deletereport", response.getStatusLine().getReasonPhrase(), response.getStatusLine().getStatusCode());
                }
            }

        } catch (IOException e) {
            LOGGER.error("Could not download report from URL {}", url, e);
            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.unknown");
        }
    }

    private void applyResolutionDate(CsvReportData report) {
        Map<String, Issue> issueIdToIssue = report.getProjects().stream()
                .flatMap(it -> it.getIssues().stream())
                .collect(Collectors.toMap(Issue::getIssueId, Function.identity()));

        if (!issueIdToIssue.isEmpty()) {
            String issueParameter = issueIdToIssue.keySet().stream().collect(Collectors.joining(","));
            List<NameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair("filter", "issue id:" + issueParameter));
            parameters.add(new BasicNameValuePair("with", "id"));
            parameters.add(new BasicNameValuePair("with", "resolved"));
            parameters.add(new BasicNameValuePair("max", String.valueOf(issueIdToIssue.size())));

            String url = urlBuilder.getIssueDetailsUrl(parameters);
            HttpGet request = new HttpGet(url);
            request.addHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*"));

            try (CloseableHttpClient httpClient = getHttpClient(urlBuilder)) {

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    StatusLine statusLine = response.getStatusLine();

                    if (!isValidResponseCode(statusLine)) {
                        LOGGER.warn("Fetching issue details from {} failed: {}", url, statusLine.getReasonPhrase());
                        EntityUtils.consumeQuietly(response.getEntity());
                        throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.issuedetails", statusLine.getReasonPhrase(), statusLine.getStatusCode());
                    }

                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    LOGGER.debug("Received JSON response {}", jsonResponse);

                    IssueDetailsResponse issueDetailsResponse = JacksonUtil.parseValue(new StringReader(jsonResponse), IssueDetailsResponse.class);
                    for (IssueDetails issueDetails : issueDetailsResponse.getIssues()) {
                        String taskId = issueDetails.getId();

                        issueDetails.getFieldList().stream()
                                .filter(issueField -> StringUtils.equals("resolved", issueField.getName()) && StringUtils.isNotEmpty(issueField.getValue()))
                                .forEach(issueField -> {
                                    try {
                                        Long resolvedTimestamp = Long.valueOf(issueField.getValue());
                                        Issue issue = issueIdToIssue.get(taskId);
                                        if (issue != null) {
                                            LocalDateTime resolvedDate = DateUtil.getDateTime(resolvedTimestamp);
                                            issue.setResolved(resolvedDate);
                                        }
                                    } catch (NumberFormatException e) {
                                        LOGGER.warn("Could not parse resolved date long from {}", issueField.getValue(), e);
                                    }
                                });
                    }
                }

            } catch (IOException e) {
                LOGGER.error("Could not fetch issue details using URL {}", url, e);
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.unknown");
            }
        }
    }

    @Override
    public List<YouTrackVersion> getSupportedVersions() {
        return Arrays.asList(YouTrackVersion.POST_2017, YouTrackVersion.POST_2018);
    }

    /**
     * YouTrack returns status 500 when no time
     * tracking occured for the requested time period
     */
    private boolean seemsToBeBlankReport(StatusLine statusLine) {
        return statusLine.getStatusCode() == 500;
    }
}
