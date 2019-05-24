package de.pbauerochse.worklogviewer.connector.v2018

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.IssueDetailsResponse
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackWorklogItem
import de.pbauerochse.worklogviewer.connector.v2018.url.UrlBuilder
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemRequest
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemResult
import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.http.HttpParams
import de.pbauerochse.worklogviewer.i18n.I18n
import de.pbauerochse.worklogviewer.isSameDayOrAfter
import de.pbauerochse.worklogviewer.isSameDayOrBefore
import de.pbauerochse.worklogviewer.report.*
import de.pbauerochse.worklogviewer.tasks.Progress
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch
import java.util.stream.Collectors

class Connector(settings: YouTrackConnectionSettings) : YouTrackConnector {

    private val urlBuilder = UrlBuilder(settings.baseUrl!!)
    private val http = Http(HttpParams(10, settings.baseUrl!!, settings.permanentToken!!))

    override fun getTimeReport(parameters: TimeReportParameters, progress: Progress): TimeReport {
        progress.setProgress(i18n("fetchingissues"), 0)
        val youtrackIssues = fetchYouTrackIssues(parameters)

        val remainingProgress = 80.0
        val progressPerIssue = if (youtrackIssues.isNotEmpty()) remainingProgress / youtrackIssues.size else 80.0

        val countDownLatch = CountDownLatch(youtrackIssues.size)
        val issues: List<Issue> = youtrackIssues
            .parallelStream()
            .map {
                val issue = fetchWithWorklogItems(it, parameters)
                countDownLatch.countDown()

                val remaining = countDownLatch.count
                val processed = youtrackIssues.size - remaining

                progress.setProgress(i18n("fetchingworklogs", youtrackIssues.size), 20 + (processed * progressPerIssue))
                return@map issue
            }
            .collect(Collectors.toList())

        progress.setProgress(i18n("done"), 100)
        return TimeReport(parameters, issues)
    }

    override fun addWorkItem(request: AddWorkItemRequest): AddWorkItemResult {
        TODO("Not implemented yet")
    }

    private fun fetchYouTrackIssues(parameters: TimeReportParameters): List<YouTrackIssue> {
        val issues = mutableListOf<YouTrackIssue>()

        var keepOnFetching = true

        while (keepOnFetching) {
            val url = urlBuilder.getIssuesUrl(parameters, issues.size)
            LOGGER.info("Fetching Issues for $parameters from url $url")

            val response = http.get(url)
            if (response.isError) {
                throw IllegalStateException("Fetching Issues failed: ${response.error}")
            }

            val issueDetailsResponse = MAPPER.readValue(response.content!!, IssueDetailsResponse::class.java)
            issues.addAll(issueDetailsResponse.issues)

            keepOnFetching = issueDetailsResponse.issues.isNotEmpty()
        }

        return issues
    }

    private fun fetchWithWorklogItems(youtrackIssue: YouTrackIssue, parameters: TimeReportParameters): Issue {
        val url = urlBuilder.getWorkItemsUrl(youtrackIssue)
        LOGGER.debug("Loading details for Issue $youtrackIssue from $url")

        val response = http.get(url)
        if (response.isError) {
            throw IllegalStateException("Fetching work items for Issue ${youtrackIssue.id} failed: ${response.error}")
        }

        val issue = Issue(
            youtrackIssue.id,
            youtrackIssue.description,
            youtrackIssue.resolutionDate,
            youtrackIssue.fields.map {
                val value = it.textValue?.let { textValue -> listOf(textValue) } ?: emptyList()
                Field(it.name, value)
            })

        val worklogItems: List<YouTrackWorklogItem> = MAPPER.readValue(response.content!!, object : TypeReference<List<YouTrackWorklogItem>>() {})
        worklogItems
            .filter { it.localDate.isSameDayOrAfter(parameters.timerange.start) && it.localDate.isSameDayOrBefore(parameters.timerange.end) }
            .map { WorklogItem(issue, User(it.author.login, it.author.fullName ?: it.author.login), it.localDate, it.duration, it.description, it.worktype?.name) }
            .forEach { issue.worklogItems.add(it) }

        return issue
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Connector::class.java)
        private val I18N = I18n("i18n/connector-2018")
        private val MAPPER = jacksonObjectMapper().findAndRegisterModules()

        private fun i18n(key: String, vararg params: Any) = I18N.get(key, *params)
    }

}