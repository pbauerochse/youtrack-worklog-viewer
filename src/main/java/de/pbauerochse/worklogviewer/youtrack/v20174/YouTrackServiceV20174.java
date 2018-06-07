package de.pbauerochse.worklogviewer.youtrack.v20174;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.http.HttpStatusCodes;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.util.DateUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.JacksonUtil;
import de.pbauerochse.worklogviewer.youtrack.*;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;
import static de.pbauerochse.worklogviewer.util.HttpClientUtil.getHttpClient;
import static de.pbauerochse.worklogviewer.util.HttpClientUtil.isValidResponseCode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class YouTrackServiceV20174 implements YouTrackService {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTrackServiceV20174.class);

    private static final YouTrackUrlBuilder URL_BUILDER = new UrlBuilder(() -> SettingsUtil.getSettings().getYouTrackConnectionSettings().getUrl(), () -> SettingsUtil.getSettings().getYouTrackConnectionSettings().getVersion());

    @SuppressWarnings("Duplicates")
    @Override
    public List<GroupByCategory> getPossibleGroupByCategories() {
        String url = URL_BUILDER.getGroupByCriteriaUrl();
        LOGGER.debug("Getting GroupBy Criteria using url {}", url);

        HttpGet request = new HttpGet(url);
        request.addHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*"));

        try (CloseableHttpClient client = getHttpClient(URL_BUILDER)) {
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

                List<FieldBasedGrouping> asGrouping = fields.stream()
                        .map(FieldBasedGrouping::new)
                        .collect(Collectors.toList());

                return ImmutableList.<GroupByCategory>builder()
                        .addAll(getStaticGroupByCategories())
                        .addAll(asGrouping)
                        .build();
            }
        } catch (IOException e) {
            LOGGER.error("Could not get GroupByCriterias from {}", url, e);
            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.unknown");
        }
    }

    private List<GroupByCategory> getStaticGroupByCategories() {
        return ImmutableList.of(
                new WorkItemBasedGrouping(new GroupByTypes("WORK_TYPE", getFormatted("grouping.worktype"))),
                new WorkItemBasedGrouping(new GroupByTypes("WORK_AUTHOR", getFormatted("grouping.author"))),
                new WorkItemBasedGrouping(new GroupByTypes("WORK_AUTHOR_AND_DATE", getFormatted("grouping.authoranddate")))
        );
    }

    @SuppressWarnings("Duplicates")
    @Override
    public ReportDetails createReport(TimeReportParameters timeReportParameters) {
        String url = URL_BUILDER.getCreateReportUrl();
        LOGGER.debug("Creating temporary timereport using url {}", url);

        CreateReportParameters payload = new CreateReportParameters(timeReportParameters);

        // request body
        HttpPost request = new HttpPost(url);
        String payloadString = JacksonUtil.writeObject(payload);
        request.setEntity(new StringEntity(payloadString, UTF_8));
        request.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8"));

        try (CloseableHttpClient httpClient = getHttpClient(URL_BUILDER)) {
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

    @Override
    public ReportDetails getReportDetails(String reportId) {
        String url = URL_BUILDER.getReportDetailsUrl(reportId);
        LOGGER.debug("Fetching report details for report {} using url {}", reportId, url);

        HttpGet request = new HttpGet(url);

        try (CloseableHttpClient httpClient = getHttpClient(URL_BUILDER)) {

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

    @SuppressWarnings("Duplicates")
    @Override
    public ByteArrayInputStream downloadReport(String reportId) {
        String url = URL_BUILDER.getDownloadReportUrl(reportId);
        LOGGER.debug("Downloading report {} using url {}", reportId, url);

        HttpGet request = new HttpGet(url);
        request.setConfig(RequestConfig.custom().setDecompressionEnabled(false).build());

        try (CloseableHttpClient httpClient = getHttpClient(URL_BUILDER)) {
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

    @SuppressWarnings("Duplicates")
    @Override
    public void deleteReport(String reportId) {
        String url = URL_BUILDER.getDeleteReportUrl(reportId);
        LOGGER.debug("Deleting report {} using url {}", reportId, url);

        HttpDelete request = new HttpDelete(url);

        try (CloseableHttpClient httpClient = getHttpClient(URL_BUILDER)) {
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

    @SuppressWarnings("Duplicates")
    @Override
    public void fetchTaskDetails(WorklogReport report) {
        Map<String, TaskWithWorklogs> taskIdToTask = report.getTasks().stream()
                .collect(toMap(TaskWithWorklogs::getIssue, Function.identity()));

        if (!taskIdToTask.isEmpty()) {
            String issueParameter = Joiner.on(',').skipNulls().join(taskIdToTask.keySet());
            List<NameValuePair> parameters = ImmutableList.<NameValuePair>builder()
                    .add(new BasicNameValuePair("filter", "issue id:" + issueParameter))
                    .add(new BasicNameValuePair("with", "id"))
                    .add(new BasicNameValuePair("with", "resolved"))
                    .add(new BasicNameValuePair("max", String.valueOf(taskIdToTask.size())))
                    .build();

            String url = URL_BUILDER.getIssueDetailsUrl(parameters);
            HttpGet request = new HttpGet(url);
            request.addHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*"));

            try (CloseableHttpClient httpClient = getHttpClient(URL_BUILDER)) {

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    StatusLine statusLine = response.getStatusLine();

                    if (!isValidResponseCode(statusLine)) {
                        LOGGER.warn("Fetching issue details from {} failed: {}", url, statusLine.getReasonPhrase());
                        EntityUtils.consumeQuietly(response.getEntity());
                        throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.issuedetails", statusLine.getReasonPhrase(), statusLine.getStatusCode());
                    }

                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    LOGGER.debug("Received JSON response {}", jsonResponse);

                    // {"issue":[{"id":"PATRICK-1","entityId":"87-2","jiraId":null,"field":[],"comment":[],"tag":[]},{"id":"PATRICK-2","entityId":"87-4","jiraId":null,"field":[{"name":"resolved","value":"1446292986266"}],"comment":[],"tag":[]}]}
                    IssueDetailsResponse issueDetailsResponse = JacksonUtil.parseValue(new StringReader(jsonResponse), IssueDetailsResponse.class);
                    for (IssueDetails issueDetails : issueDetailsResponse.getIssues()) {
                        String taskId = issueDetails.getId();

                        issueDetails.getFieldList().stream()
                                .filter(issueField -> StringUtils.equals("resolved", issueField.getName()) && StringUtils.isNotEmpty(issueField.getValue()))
                                .forEach(issueField -> {
                                    try {
                                        Long resolvedTimestamp = Long.valueOf(issueField.getValue());
                                        TaskWithWorklogs taskWithWorklogs = taskIdToTask.get(taskId);

                                        if (taskWithWorklogs != null) {
                                            LocalDateTime resolvedDate = DateUtil.getDateTime(resolvedTimestamp);
                                            taskWithWorklogs.setResolved(resolvedDate);
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
        return ImmutableList.of(YouTrackVersion.POST_2017, YouTrackVersion.POST_2018);
    }

    /**
     * YouTrack returns status 500 when no time
     * tracking occured for the requested time period
     */
    private boolean seemsToBeBlankReport(StatusLine statusLine) {
        return statusLine.getStatusCode() == HttpStatusCodes.STATUS_CODE_SERVER_ERROR;
    }
}
