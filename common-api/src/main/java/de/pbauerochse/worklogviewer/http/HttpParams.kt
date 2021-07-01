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
            BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, sdch"),
            BasicHeader(HttpHeaders.ACCEPT, "application/json"),
            BasicHeader(HttpHeaders.CACHE_CONTROL, "no-cache"),
            BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer $youtrackPermanentToken")
        )
    }
}