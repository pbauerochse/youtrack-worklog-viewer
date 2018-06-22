package de.pbauerochse.worklogviewer.connector.v2017.url

import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Constructs the endpoint URLs for
 * a 2017.4 YouTrack Version up to
 * but not including 2018.1
 */
internal open class V2017UrlBuilder(private val baseUrl : URL) : UrlBuilder {

    override fun getGroupByParametersUrl(): URL =
        generateUrl("/api/filterFields?fieldTypes=version%5B1%5D&fieldTypes=ownedField%5B1%5D&fieldTypes=state%5B1%5D&fieldTypes=user%5B1%5D&fieldTypes=enum%5B1%5D&fieldTypes=date&fieldTypes=integer&fieldTypes=float&fieldTypes=period&fieldTypes=project&fields=id,\$type,presentation,name,aggregateable,sortable,customField(id,fieldType(id),name,localizedName),projects(id,name)&includeNonFilterFields=true")

    override fun getCreateReportUrl(): URL =
        generateUrl("/api/reports?fields=\$type,authors(fullName,id,login,ringId),effectiveQuery,effectiveQueryUrl,id,invalidationInterval,name,own,owner(id,login),pinned,projects(id,name,shortName),query,sprint(id,name),status(calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage),visibleTo(id,name),wikifiedError,windowSize,workTypes(id,name)")

    override fun getReportDetailsUrl(reportId: String): URL =
        generateUrl("/api/reports/$reportId/status?fields=calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage")

    override fun getDownloadReportCsvUrl(reportId: String): URL =
        generateUrl("/api/reports/$reportId/export")

    override fun getDeleteReportUrl(reportId: String): URL =
        generateUrl("/api/reports/$reportId?fields=\$type,authors(fullName,id,login,ringId),effectiveQuery,effectiveQueryUrl,id,invalidationInterval,name,own,owner(id,login),pinned,projects(id,name,shortName),query,sprint(id,name),status(calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage),visibleTo(id,name),wikifiedError,windowSize,workTypes(id,name)")

    override fun getIssueDetailsUrl(requestParams: List<NameValuePair>): URL {
        val urlEncoded = URLEncodedUtils.format(requestParams, StandardCharsets.UTF_8)
        return generateUrl("/rest/issue?$urlEncoded")
    }

    internal fun generateUrl(path : String) : URL {
        val baseUrlAsString = baseUrl.toExternalForm().trimEnd('/')
        val pathCleansed = path.trim().trimStart('/')
        return URL("$baseUrlAsString/$pathCleansed")
    }
}