package de.pbauerochse.worklogviewer.datasource.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.pbauerochse.worklogviewer.datasource.AddWorkItemRequest
import de.pbauerochse.worklogviewer.datasource.AddWorkItemResult
import de.pbauerochse.worklogviewer.datasource.ConnectionSettings
import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource
import de.pbauerochse.worklogviewer.datasource.api.domain.*
import de.pbauerochse.worklogviewer.datasource.api.domain.adapters.IssueAdapter
import de.pbauerochse.worklogviewer.datasource.api.domain.adapters.WorkItemAdapter
import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.http.HttpParams
import de.pbauerochse.worklogviewer.i18n.I18n
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.timereport.*
import org.apache.http.HttpHeaders
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.slf4j.LoggerFactory
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter

/**
 * YouTrack DataSource that uses the REST API published in 2019
 */
class RestApiDataSource(settings: ConnectionSettings) : TimeTrackingDataSource {

    private val http = Http(HttpParams(60, settings.baseUrl!!, settings.permanentToken!!))

    private val issueUrlBuilder: (issue: YouTrackIssue) -> URL = { URL(settings.baseUrl, "/issue/${it.idReadable}#tab=Time%20Tracking") }
    private val belongsToCurrentUserFunction: (workItem: YouTrackIssueWorkItem) -> Boolean = { settings.username == it.author?.login }

    override fun getTimeReport(parameters: TimeReportParameters, progress: Progress): TimeReport {
        LOGGER.info("Fetching TimeReport for ${parameters.timerange}")
        val youtrackWorkItems = fetchWorkItems(parameters.timerange, progress.subProgress(70))

        // convert to IssueWithWorkItems
        val issuesWithWorkItems = youtrackWorkItems
            .groupBy { it.issue.id }
            .map { (_, workItemList) ->
                val youtrackIssue = workItemList.first().issue
                val issue = IssueAdapter(youtrackIssue, issueUrlBuilder.invoke(youtrackIssue))
                val workItems = workItemList.map { WorkItemAdapter(it, belongsToCurrentUserFunction.invoke(it)) }
                return@map IssueWithWorkItems(issue, workItems)
            }

        progress.setProgress(i18n("connector.rest.done"), 100)
        return TimeReport(parameters, issuesWithWorkItems)
    }

    /**
     * https://www.jetbrains.com/help/youtrack/devportal/resource-api-issues-issueID-timeTracking-workItems.html#create-IssueWorkItem-method
     */
    override fun addWorkItem(request: AddWorkItemRequest, progress: Progress): AddWorkItemResult {
        val url = "/api/issues/${request.issueId}/timeTracking/workItems?fields=$WORKITEM_FIELDS"

        val user = getMe()
        val workItemType = request.workItemType?.let {
            YouTrackWorkItemType(
                id = it.id,
                name = it.label
            )
        }
        val youtrackRequest = YouTrackCreateWorkItemRequest(request.date, request.durationInMinutes, user, request.description, workItemType)
        val serialized = MAPPER.writeValueAsString(youtrackRequest)

        val payload = StringEntity(serialized, StandardCharsets.UTF_8)
        payload.contentType = BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        payload.contentEncoding = BasicHeader(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
        val response = http.post(url, payload)
        if (response.isError) {
            LOGGER.error("Got Error Response Message from YouTrack while pushing WorkItem $serialized to URL $url: ${response.statusLine.statusCode} ${response.error}")
            throw IllegalStateException(i18n("connector.rest.addworkitem.http.post.error", response.error))
        }

        val newYouTrackWorkItem = MAPPER.readValue(response.content!!, YouTrackIssueWorkItem::class.java)
        val youTrackIssue = newYouTrackWorkItem.issue

        return AddWorkItemResult(
            issue = IssueAdapter(youTrackIssue, issueUrlBuilder.invoke(youTrackIssue)),
            addedWorkItem = WorkItemAdapter(newYouTrackWorkItem, belongsToCurrentUserFunction.invoke(newYouTrackWorkItem))
        )
    }

    override fun loadIssue(id: String, progress: Progress): Issue {
        val url = "/api/issues/$id?fields=$ISSUE_FIELDS"

        progress.setProgress(i18n("connector.rest.issue.byid", id), 1.0)
        val response = http.get(url)
        if (response.isError) {
            LOGGER.error("Got Error Response Message from YouTrack while loading single Issue $id from URL $url: ${response.statusLine.statusCode} ${response.error}")
            throw IllegalStateException(i18n("connector.rest.issue.details.http.error", id, response.error))
        }

        val youtrackIssue = MAPPER.readValue(response.content!!, YouTrackIssue::class.java)
        progress.setProgress(i18n("connector.rest.done"), 100.0)
        return IssueAdapter(youtrackIssue, issueUrlBuilder.invoke(youtrackIssue))
    }

    override fun loadIssuesByIds(issueIds: Set<String>, progress: Progress): List<Issue> {
        val query = "Issue ID: ${issueIds.joinToString(",")}"
        return searchIssues(query, 0, issueIds.size, progress)
    }

    override fun searchIssues(query: String, offset: Int, maxResults: Int, progress: Progress): List<Issue> {
        LOGGER.info("Searching Issues with query='$query' and offset=$offset")
        progress.setProgress(i18n("connector.rest.issue.search.progress.query", query), 1)

        val queryParam = URLEncoder.encode(query, StandardCharsets.UTF_8)
        val url = "/api/issues?fields=$ISSUE_FIELDS&\$top=$maxResults&\$skip=$offset&query=$queryParam"

        val response = http.get(url)
        if (response.isError) {
            LOGGER.error("Got Error Response Message from YouTrack while fetching with URL $url: ${response.statusLine.statusCode} ${response.error}")
            throw IllegalStateException(i18n("connector.rest.issue.search.http.error", query, response.error))
        }

        val issues: List<YouTrackIssue> = MAPPER.readValue(response.content!!, object : TypeReference<List<YouTrackIssue>>() {})
        LOGGER.debug("Got ${issues.size} Issues for query '$query'")
        progress.setProgress(i18n("connector.rest.done"), 100)

        return issues.map { IssueAdapter(it, issueUrlBuilder.invoke(it)) }
    }

    private fun loadWorkItems(issue: Issue, progress: Progress): IssueWithWorkItems = loadWorkItems(issue, null, progress)
    override fun loadWorkItems(issue: Issue, timeRange: TimeRange?, progress: Progress): IssueWithWorkItems {
        LOGGER.info("Loading WorkItems for Issue $issue")

        var keepOnFetching = true
        val workItems = mutableListOf<YouTrackIssueWorkItem>()
        val query = URLEncoder.encode("Issue ID:${issue.humanReadableId}", StandardCharsets.UTF_8)
        val dateUrlParams = timeRange?.let {
            val startDateFormatted = it.start.format(DATE_FORMATTER)
            val endDateFormatted = it.end.format(DATE_FORMATTER)
            "&startDate=$startDateFormatted&endDate=$endDateFormatted"
        } ?: ""

        while (keepOnFetching) {
            val url = "/api/workItems?query=$query&\$skip=${workItems.size}&\$top=$MAX_WORKITEMS_PER_BATCH&fields=${WORKITEM_FIELDS}$dateUrlParams"
//            val url = "/api/issues/${issue.id}/timeTracking/workItems?\$skip=${workItems.size}&\$top=$MAX_WORKITEMS_PER_BATCH&fields=$WORKITEM_FIELDS"

            val response = http.get(url)
            if (response.isError) {
                LOGGER.error("Got Error Response Message from YouTrack while fetching with URL $url: ${response.statusLine.statusCode} ${response.error}")
                throw IllegalStateException(i18n("connector.rest.workitems.http.error", response.error))
            }

            val currentWorkItemsBatch: List<YouTrackIssueWorkItem> = MAPPER.readValue(response.content!!, object : TypeReference<List<YouTrackIssueWorkItem>>() {})
            LOGGER.debug("Got ${currentWorkItemsBatch.size} WorkItems")

            workItems.addAll(currentWorkItemsBatch)
            keepOnFetching = currentWorkItemsBatch.size == MAX_WORKITEMS_PER_BATCH

            progress.incrementProgress(1)
        }

        return IssueWithWorkItems(issue, workItems.map { WorkItemAdapter(it, belongsToCurrentUserFunction.invoke(it)) })
    }

    override fun getWorkItemTypes(projectId: String, progress: Progress): List<WorkItemType> {
        LOGGER.info("Loading WorkItemTypes for project $projectId")
        val url = "/api/admin/projects/$projectId/timeTrackingSettings/workItemTypes?fields=id,name"

        progress.setProgress(i18n("connector.rest.workitemtypes.progress.loading"), 1)
        val response = http.get(url)
        if (response.isError) {
            LOGGER.error("Got Error Response Message from YouTrack while fetching with URL $url: ${response.statusLine.statusCode} ${response.error}")
            throw IllegalStateException(i18n("connector.rest.workitemtypes.http.error", response.error))
        }

        val workItemTypes: List<YouTrackWorkItemType> = MAPPER.readValue(response.content!!, object : TypeReference<List<YouTrackWorkItemType>>() {})
        progress.setProgress(i18n("connector.rest.done"), 100)

        return workItemTypes.map {
            WorkItemType(it.id, it.name ?: it.id)
        }
    }

    private fun getMe(): YouTrackUser {
        LOGGER.debug("Getting myself as YouTrack User")
        val url = "/api/admin/users/me?fields=$USER_FIELDS"
        val response = http.get(url)
        if (response.isError) {
            LOGGER.error("Got Error Response Message from YouTrack while fetching Me $url: ${response.statusLine.statusCode} ${response.error}")
            throw IllegalStateException(i18n("connector.rest.getme.http.error", response.error))
        }

        return MAPPER.readValue(response.content!!, YouTrackUser::class.java)
    }

    private fun fetchWorkItems(timerange: TimeRange, progress: Progress): List<YouTrackIssueWorkItem> {
        LOGGER.info("Fetching WorkItems for $timerange")
        progress.setProgress(i18n("connector.rest.workitems.progress.loading", timerange.formattedForLocale), 1)

        val startDateFormatted = timerange.start.format(DATE_FORMATTER)
        val endDateFormatted = timerange.end.format(DATE_FORMATTER)

        var keepOnFetching = true
        val workItems = mutableListOf<YouTrackIssueWorkItem>()

        while (keepOnFetching) {
            val url = "/api/workItems?\$top=$MAX_WORKITEMS_PER_BATCH&\$skip=${workItems.size}&fields=$WORKITEM_FIELDS&startDate=$startDateFormatted&endDate=$endDateFormatted"

            val response = http.get(url)
            if (response.isError) {
                LOGGER.error("Got Error Response Message from YouTrack while fetching with URL $url: ${response.statusLine.statusCode} ${response.error}")
                throw IllegalStateException(i18n("connector.rest.workitems.http.error", response.error))
            }

            val currentWorkItemsBatch: List<YouTrackIssueWorkItem> = MAPPER.readValue(response.content!!, object : TypeReference<List<YouTrackIssueWorkItem>>() {})
            LOGGER.debug("Got ${currentWorkItemsBatch.size} WorkItems")

            progress.setProgress(i18n("connector.rest.workitems.progress.loading", timerange.formattedForLocale), 10)
            workItems.addAll(currentWorkItemsBatch)
            keepOnFetching = currentWorkItemsBatch.size == MAX_WORKITEMS_PER_BATCH
        }

        progress.setProgress(i18n("connector.rest.done"), 100)
        return workItems
    }

    companion object {
        val I18N = I18n("i18n/connectors/rest-api")
        private val LOGGER = LoggerFactory.getLogger(RestApiDataSource::class.java)
        private val DATE_FORMATTER = DateTimeFormatter.ISO_DATE
        private val MAPPER = jacksonObjectMapper().findAndRegisterModules()

        private fun i18n(key: String, vararg params: Any) = I18N.get(key, *params)

        private const val MAX_WORKITEMS_PER_BATCH = 400
        private const val USER_FIELDS = "id,login,fullName,email"
        private const val ISSUE_FIELDS = "id,idReadable,tags(color(background,foreground),name),resolved,project(id,shortName,name),summary,wikifiedDescription,customFields(name,localizedName,value(name))"
        private const val WORKITEM_FIELDS = "id,author($USER_FIELDS),creator($USER_FIELDS),type(id,name),text,duration(minutes,presentation),date,issue($ISSUE_FIELDS)"
    }
}
