package de.pbauerochse.worklogviewer.connector.v2018

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.pbauerochse.worklogviewer.connector.GroupByParameter
import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.v2018.domain.groupby.GroupingField
import de.pbauerochse.worklogviewer.connector.v2018.domain.grouping.*
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.IssueDetailsResponse
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackWorklogItem
import de.pbauerochse.worklogviewer.connector.v2018.url.UrlFactory
import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.isSameDayOrAfter
import de.pbauerochse.worklogviewer.isSameDayOrBefore
import de.pbauerochse.worklogviewer.report.*
import de.pbauerochse.worklogviewer.tasks.Progress
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.stream.Collectors

/**
 * YouTrackConnector for YouTrack 2018.2
 */
class Connector(youTrackConnectionSettings: YouTrackConnectionSettings) : YouTrackConnector {

    private val urlFactory = UrlFactory(youTrackConnectionSettings.baseUrl, youTrackConnectionSettings.workdateFieldName)
    private val http: Http = Http(youTrackConnectionSettings)

    override fun getGroupByParameters(): List<GroupByParameter> {
        val url = urlFactory.getGroupByParametersUrl()
        LOGGER.debug("Getting GroupByParameters from $url")

        val response = http.get(url)
        if (response.isError) {
            throw IllegalStateException("Fetching GroupByParameters failed: ${response.error}")
        }

        val groupingFields = OBJECT_MAPPER
            .readValue<List<GroupingField>>(response.content!!, object : TypeReference<List<GroupingField>>() {})
            .filter { it.isProcessableFieldGrouping }
            .map { FieldBasedGrouping(it) }

        return CONSTANT_GROUP_BY_PARAMETERS + groupingFields
    }

    override fun getTimeReport(parameters: TimeReportParameters, progress: Progress): TimeReport {
        progress.setProgress(Translations.i18n.get("fetchingissues"), 0)
        val youtrackIssues = fetchYouTrackIssues(parameters)

        val remainingProgress = 80.0
        val progressPerIssue = if (youtrackIssues.isNotEmpty()) remainingProgress / youtrackIssues.size else 80.0

        val countDownLatch = CountDownLatch(youtrackIssues.size)
        val issues = youtrackIssues
            .parallelStream()
            .map {
                val issue = fetchWithWorklogItems(it, parameters)
                countDownLatch.countDown()

                val remaining = countDownLatch.count
                val processed = youtrackIssues.size - remaining

                progress.setProgress(Translations.i18n.get("fetchingworklogs", youtrackIssues.size), 20 + (processed * progressPerIssue))
                return@map issue
            }
            .collect(Collectors.toList())

        progress.setProgress(Translations.i18n.get("done"), 100)
        return TimeReport(parameters, issues)
    }

    private fun fetchYouTrackIssues(parameters: TimeReportParameters): List<YouTrackIssue> {
        val issues = mutableListOf<YouTrackIssue>()

        var keepOnFetching = true

        while (keepOnFetching) {
            val url = urlFactory.getIssuesUrl(parameters, issues.size)
            LOGGER.info("Fetching Issues for $parameters from url $url")

            val response = http.get(url)
            if (response.isError) {
                throw IllegalStateException("Fetching Issues failed: ${response.error}")
            }

            val issueDetailsResponse = OBJECT_MAPPER.readValue(response.content!!, IssueDetailsResponse::class.java)
            issues.addAll(issueDetailsResponse.issues)

            keepOnFetching = issueDetailsResponse.issues.isNotEmpty()
        }

        return issues
    }

    private fun fetchWithWorklogItems(youtrackIssue: YouTrackIssue, parameters: TimeReportParameters): Issue {
        val url = urlFactory.getWorkItemsUrl(youtrackIssue)
        LOGGER.debug("Loading details for Issue $youtrackIssue from $url")

        val response = http.get(url)
        if (response.isError) {
            throw IllegalStateException("Fetching work items for Issue ${youtrackIssue.id} failed: ${response.error}")
        }

        val fields = youtrackIssue.fields.map { Field(it.name, it.toGroupByKey()) }
        val issue = Issue(youtrackIssue.id, youtrackIssue.description, youtrackIssue.resolutionDate, fields)

        val worklogItems: List<YouTrackWorklogItem> = OBJECT_MAPPER.readValue(response.content!!, object : TypeReference<List<YouTrackWorklogItem>>() {})

        worklogItems
            .filter {
                it.localDate.isSameDayOrAfter(parameters.timerange.start) && it.localDate.isSameDayOrBefore(parameters.timerange.end)
            }
            .map {
                val groupingKey = getGroupingKey(it, issue, youtrackIssue, parameters) ?: "---"
                WorklogItem(issue, User(it.author.login), it.localDate, it.duration, it.description, it.worktype?.name, groupingKey)
            }
            .forEach { issue.worklogItems.add(it) }

        return issue
    }

    private fun getGroupingKey(worklogItem: YouTrackWorklogItem, issue: Issue, youtrackIssue: YouTrackIssue, parameters: TimeReportParameters): String? {
        return when (parameters.groupByParameter) {
            null -> null
            else -> (parameters.groupByParameter as Grouping).getGroupingKey(worklogItem, issue, youtrackIssue)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Connector::class.java)
        private val OBJECT_MAPPER = ObjectMapper()
        private val CONSTANT_GROUP_BY_PARAMETERS = listOf<GroupByParameter>(
            WorkItemTypeGrouping, WorkItemAuthorGrouping, ProjectGrouping
        )
    }
}
