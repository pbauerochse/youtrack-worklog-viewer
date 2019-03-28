package de.pbauerochse.worklogviewer.connector.v2017.url

import org.apache.http.NameValuePair
import java.net.URL

/**
 * Generates the endpoint URLs
 * for the YouTrack instance
 */
interface UrlBuilder {

    fun getGroupByParametersUrl(): URL

    fun getCreateReportUrl(): URL

    fun getReportDetailsUrl(reportId: String): URL

    fun getDownloadReportCsvUrl(reportId: String): URL

    fun getDeleteReportUrl(reportId: String): URL

    fun getIssueDetailsUrl(requestParams: List<NameValuePair>): URL

    fun generateUrl(path : String) : URL

}