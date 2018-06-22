package de.pbauerochse.worklogviewer.connector.v2017.url

import java.net.URL

/**
 * Constructs the endpoint URLs for
 * YouTrack Version 2018.1
 */
internal class V2018UrlBuilder(baseURL: URL) : V2017UrlBuilder(baseURL) {

    override fun getDownloadReportCsvUrl(reportId: String): URL =
            generateUrl("/api/reports/$reportId/export/csv")

}