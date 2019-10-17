package de.pbauerochse.worklogviewer.http

import org.apache.http.Header
import org.apache.http.HttpHeaders
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import org.apache.http.message.BasicHeader
import java.net.ProxySelector
import java.net.URL

/**
 *
 */
class HttpParams(
    private val connectTimeoutInSeconds: Int,
    private val youtrackBaseUrl : URL,
    private val youtrackPermanentToken : String
) {

    internal fun httpClientBuilder() : HttpClientBuilder {
        val requestConfig = RequestConfig
            .custom()
            .setConnectTimeout(connectTimeoutInSeconds * 1000)
            .setConnectionRequestTimeout(connectTimeoutInSeconds * 1000)
            .build()

        val routePlanner = SystemDefaultRoutePlanner(ProxySelector.getDefault())
        return HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .setRoutePlanner(routePlanner)
            .setDefaultHeaders(httpHeaders)
    }

    internal fun buildUrl(path: String): URL {
        val baseUrlAsString = youtrackBaseUrl.toExternalForm().trimEnd('/')
        val pathCleansed = path.trim().trimStart('/')
        return URL("$baseUrlAsString/$pathCleansed")
    }

    private val httpHeaders: List<Header> by lazy {
        listOf(
            BasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36"),
            BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, sdch"),
            BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4"),
            BasicHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*"),
            BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer $youtrackPermanentToken")
        )
    }
}