package de.pbauerochse.worklogviewer.connector.v2018.url

import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter

/**
 * Provides the URLs
 */
class UrlFactory(private val baseUrl: URL) {

    fun getGroupByParametersUrl(): URL =
        generateUrl("/api/filterFields?fieldTypes=version%5B1%5D&fieldTypes=ownedField%5B1%5D&fieldTypes=state%5B1%5D&fieldTypes=user%5B1%5D&fieldTypes=enum%5B1%5D&fieldTypes=date&fieldTypes=integer&fieldTypes=float&fieldTypes=period&fieldTypes=project&fields=id,\$type,presentation,name,aggregateable,sortable,customField(id,fieldType(id),name,localizedName),projects(id,name)&includeNonFilterFields=true")

    fun getIssuesUrl(parameters: TimeReportParameters, numberOfIssuesToSkip: Int): URL {
        val startDate = parameters.timerange.start.format(QUERY_DATE_FORMAT)
        val endDate = parameters.timerange.end.format(QUERY_DATE_FORMAT)
        val query = URLEncoder.encode("$WORK_DATE_FIELD: $startDate .. $endDate", StandardCharsets.UTF_8.name())
        return generateUrl("/rest/issue?filter=$query&max=500&after=$numberOfIssuesToSkip")
    }

    fun getWorkItemsUrl(issue: YouTrackIssue): URL =
        generateUrl("/rest/issue/${issue.id}/timetracking/workitem")

    private fun generateUrl(path: String): URL {
        val baseUrlAsString = baseUrl.toExternalForm().trimEnd('/')
        val pathCleansed = path.trim().trimStart('/')
        return URL("$baseUrlAsString/$pathCleansed")
    }

    companion object {
        private const val WORK_DATE_FIELD = "work date"
        //        private const val WORK_DATE_FIELD = "Arbeitsdatum" // use this for debugging with a german youtrack < build 43006 for now see https://youtrack.jetbrains.com/issue/JT-47943
        private val QUERY_DATE_FORMAT = DateTimeFormatter.ISO_DATE
    }
}