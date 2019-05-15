package de.pbauerochse.worklogviewer.connector.v2018.url

import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter

class UrlBuilder(private val baseUrl: URL) {

    fun getIssuesUrl(parameters: TimeReportParameters, numberOfIssuesToSkip: Int): URL {
        val startDate = parameters.timerange.start.format(QUERY_DATE_FORMAT)
        val endDate = parameters.timerange.end.format(QUERY_DATE_FORMAT)
        val query = URLEncoder.encode("$WORK_DATE_FIELD: $startDate .. $endDate", StandardCharsets.UTF_8.name())
        return generateUrl("/rest/issue?filter=$query&max=500&after=$numberOfIssuesToSkip")
    }

    fun getWorkItemsUrl(issue: YouTrackIssue): URL = getAddWorkItemUrl(issue.id)
    fun getAddWorkItemUrl(issueId: String): URL =
        generateUrl("/rest/issue/$issueId/timetracking/workitem")

    private fun generateUrl(path: String): URL {
        val baseUrlAsString = baseUrl.toExternalForm().trimEnd('/')
        val pathCleansed = path.trim().trimStart('/')
        return URL("$baseUrlAsString/$pathCleansed")
    }

    companion object {
        private const val WORK_DATE_FIELD = "work date"
        private val QUERY_DATE_FORMAT = DateTimeFormatter.ISO_DATE
    }

}