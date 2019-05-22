package de.pbauerochse.worklogviewer.connector.v2019

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.v2019.model.YouTrackCreateWorkItemRequest
import de.pbauerochse.worklogviewer.connector.v2019.model.YouTrackIssue
import de.pbauerochse.worklogviewer.connector.v2019.model.YouTrackUser
import de.pbauerochse.worklogviewer.connector.v2019.model.YouTrackWorkItem
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemRequest
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemResult
import de.pbauerochse.worklogviewer.connector.workitem.MinimalWorklogItem
import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.http.HttpParams
import de.pbauerochse.worklogviewer.i18n.I18n
import de.pbauerochse.worklogviewer.report.*
import de.pbauerochse.worklogviewer.tasks.Progress
import org.apache.http.HttpHeaders
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter

class Connector(settings: YouTrackConnectionSettings) : YouTrackConnector {

    private val http = Http(HttpParams(10, settings.baseUrl!!, settings.permanentToken!!))

    override fun getTimeReport(parameters: TimeReportParameters, progress: Progress): TimeReport {
        LOGGER.info("Fetching TimeReport for ${parameters.timerange}")
        val workItems = fetchWorkItems(parameters.timerange, progress.subProgress(70))
        val issues = createIssues(workItems, progress.subProgress(30))

        progress.setProgress(i18n("done"), 100)
        return TimeReport(parameters, issues)
    }

    override fun addWorkItem(request: AddWorkItemRequest): AddWorkItemResult {
        val url = "/api/issues/${request.issueId}/timeTracking/workItems?fields=$WORKITEM_FIELDS"

        val user = getMe()
        val youtrackRequest = YouTrackCreateWorkItemRequest(request.date, request.durationInMinutes, user, request.description)
        val serialized = MAPPER.writeValueAsString(youtrackRequest)

        val payload = StringEntity(serialized, StandardCharsets.UTF_8)
        payload.contentType = BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        payload.contentEncoding = BasicHeader(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
        val response = http.post(url, payload)
        if (response.isError) {
            LOGGER.error("Got Error Response Message from YouTrack while pushing WorkItem $serialized to URL $url: ${response.statusLine.statusCode} ${response.error}")
            return AddWorkItemResult.error(i18n("addworkitem.post.error", response.error))
        }

        val newYouTrackWorkItem = MAPPER.readValue(response.content!!, YouTrackWorkItem::class.java)
        val minimalWorklogItem = MinimalWorklogItem(
                newYouTrackWorkItem.issue.id,
                getUser(newYouTrackWorkItem.author),
                newYouTrackWorkItem.date!!.toLocalDate(),
                newYouTrackWorkItem.duration.minutes,
                newYouTrackWorkItem.text,
                newYouTrackWorkItem.type?.name
        )

        return AddWorkItemResult.success(minimalWorklogItem)
    }

    private fun getMe(): YouTrackUser {
        LOGGER.debug("Getting myself as YouTrack User")
        val url = "/api/admin/users/me?fields=$USER_FIELDS"
        val response = http.get(url)
        if (response.isError) {
            LOGGER.error("Got Error Response Message from YouTrack while fetching Me $url: ${response.statusLine.statusCode} ${response.error}")
            throw IllegalStateException(i18n("addworkitem.getme.error", response.error))
        }

        return MAPPER.readValue(response.content!!, YouTrackUser::class.java)
    }

    private fun fetchWorkItems(timerange: TimeRange, progress: Progress): List<YouTrackWorkItem> {
        LOGGER.info("Fetching WorkItems for $timerange")
        progress.setProgress(i18n("fetching.workitems", timerange.formattedForLocale), 1)

        val startDateFormatted = timerange.start.format(DATE_FORMATTER)
        val endDateFormatted = timerange.end.format(DATE_FORMATTER)

        var keepOnFetching = true
        val workItems = mutableListOf<YouTrackWorkItem>()

        while (keepOnFetching) {
            val url = "/api/workItems?\$top=$MAX_WORKITEMS_PER_BATCH&\$skip=${workItems.size}&fields=$WORKITEM_FIELDS&startDate=$startDateFormatted&endDate=$endDateFormatted"

            val response = http.get(url)
            if (response.isError) {
                LOGGER.error("Got Error Response Message from YouTrack while fetching with URL $url: ${response.statusLine.statusCode} ${response.error}")
                throw IllegalStateException(i18n("fetching.workitems.error", response.error))
            }

            val currentWorkItemsBatch: List<YouTrackWorkItem> = MAPPER.readValue(response.content!!, object : TypeReference<List<YouTrackWorkItem>>() {})
            LOGGER.debug("Got ${currentWorkItemsBatch.size} WorkItems")

            progress.setProgress(i18n("fetching.workitems", timerange.formattedForLocale), 10)
            workItems.addAll(currentWorkItemsBatch)
            keepOnFetching = currentWorkItemsBatch.size == MAX_WORKITEMS_PER_BATCH
        }

        progress.setProgress(i18n("done"), 100)
        return workItems
    }

    private fun createIssues(workItems: List<YouTrackWorkItem>, progress: Progress): List<Issue> {
        val subprogress = progress.subProgress(workItems.size)

        return workItems.asSequence()
            .groupBy { it.issue.id }
            .map {
                val youtrackIssue = it.value[0].issue
                subprogress.incrementProgress(i18n("converting.issues", youtrackIssue.id), 1)
                createIssue(youtrackIssue, it.value)
            }
    }

    private fun createIssue(youtrackIssue: YouTrackIssue, workItems: List<YouTrackWorkItem>): Issue {
        val fields = youtrackIssue.customFields.map { customField ->
            val fieldValues = customField.values.asSequence()
                .filter { it.value.isNullOrBlank().not() }
                .mapNotNull { it.value }
                .toList()

            Field(customField.name!!, fieldValues)
        }

        val issue = Issue(
            id = youtrackIssue.id,
            description = youtrackIssue.summary ?: i18n("issue.nosummary"),
            resolutionDate = youtrackIssue.resolveDate?.toLocalDateTime(),
            fields = fields
        )

        val worklogItems = workItems.map {
            val user = getUser(it.author)
            WorklogItem(issue, user, it.date!!.toLocalDate(), it.duration.minutes, it.text, it.type?.name)
        }

        return issue.apply {
            this.worklogItems.addAll(worklogItems)
        }
    }

    private fun getUser(author: YouTrackUser?): User {
        val login = author?.login ?: i18n("user.unknown.login")
        val displayName = author?.fullName ?: i18n("user.unknown.displayname")
        return User(login, displayName)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Connector::class.java)
        private val DATE_FORMATTER = DateTimeFormatter.ISO_DATE
        private val I18N = I18n("i18n/connector-2019")
        private val MAPPER = jacksonObjectMapper().findAndRegisterModules()

        private fun i18n(key: String, vararg params: Any) = I18N.get(key, *params)

        private const val MAX_WORKITEMS_PER_BATCH = 400
        private const val USER_FIELDS = "id,login,fullName,email"
        private const val ISSUE_FIELDS = "idReadable,resolved,project(shortName,name),summary,customFields(name,localizedName,aliases,value(name))"
        private const val WORKITEM_FIELDS = "author($USER_FIELDS),creator($USER_FIELDS),type(name),text,duration(minutes,presentation),date,issue($ISSUE_FIELDS)"
    }
}
