package de.pbauerochse.worklogviewer.youtrack.v20173;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import de.pbauerochse.worklogviewer.util.DateUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.JacksonUtil;
import de.pbauerochse.worklogviewer.youtrack.*;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;
import de.pbauerochse.worklogviewer.youtrack.issuedetails.IssueDetails;
import de.pbauerochse.worklogviewer.youtrack.issuedetails.IssueDetailsResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
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

import static de.pbauerochse.worklogviewer.util.HttpClientUtil.getHttpClient;
import static de.pbauerochse.worklogviewer.util.HttpClientUtil.isValidResponseCode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class YouTrackServiceV20173 implements YouTrackService {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTrackServiceV20173.class);

    private static final YouTrackUrlBuilder URL_BUILDER = new UrlBuilder();

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

                return JacksonUtil.parseValue(new StringReader(jsonResponse), new TypeReference<List<GroupByCategory>>() {
                });
            }
        } catch (IOException e) {
            LOGGER.error("Could not get GroupByCriterias from {}", url, e);
            throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.unknown");
        }
    }

    @Override
    public ReportDetails createReport(TimereportContext timereportContext) {
        String url = URL_BUILDER.getCreateReportUrl();
        LOGGER.debug("Creating temporary timereport using url {}", url);

        CreateReportParameters payload = new CreateReportParameters(timereportContext);

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
                return JacksonUtil.parseValue(new StringReader(jsonResponse), ReportDetailsResponse.class);
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

        try (CloseableHttpClient httpClient = getHttpClient(URL_BUILDER)) {
            return httpClient.execute(request, response -> {
                ByteArrayInputStream inputStream;

                try (CloseableHttpResponse closeableHttpResponse = (CloseableHttpResponse) response) {
                    HttpEntity entity = closeableHttpResponse.getEntity();
                    byte[] bytes = EntityUtils.toByteArray(entity);
                    inputStream = new ByteArrayInputStream(bytes);
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
    public YouTrackVersion getVersion() {
        return YouTrackVersion.PRE_2017;
    }

    @Override
    public List<YouTrackAuthenticationMethod> getValidAuthenticationMethods() {
        return ImmutableList.of(
                YouTrackAuthenticationMethod.HTTP_API,
                YouTrackAuthenticationMethod.OAUTH2,
                YouTrackAuthenticationMethod.PERMANENT_TOKEN
        );
    }


}
