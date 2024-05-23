package de.pbauerochse.worklogviewer.http

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner
import org.apache.hc.core5.http.Header
import org.apache.hc.core5.http.HttpHeaders
import org.apache.hc.core5.http.message.BasicHeader
import java.net.ProxySelector
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 *
 */
class HttpParams(
    private val connectTimeoutInSeconds: Int,
    private val youtrackBaseUrl : URL,
    private val youtrackPermanentToken : String
) {

    internal fun httpClientBuilder() : HttpClientBuilder {
        val connectionConfig = ConnectionConfig.custom()
            .setConnectTimeout(connectTimeoutInSeconds.toLong(), TimeUnit.SECONDS)
            .setSocketTimeout(connectTimeoutInSeconds, TimeUnit.SECONDS)
            .setTimeToLive(connectTimeoutInSeconds * 5L, TimeUnit.SECONDS)
            .build()

        val requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(connectTimeoutInSeconds.toLong(), TimeUnit.SECONDS)
            .setResponseTimeout(connectTimeoutInSeconds * 2L, TimeUnit.SECONDS)
            .build()

        val connectionManager = PoolingHttpClientConnectionManager().apply {
            setDefaultConnectionConfig(connectionConfig)
        }

        val routePlanner = SystemDefaultRoutePlanner(ProxySelector.getDefault())
        return HttpClients.custom()
            .setConnectionManager(connectionManager)
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