package de.pbauerochse.worklogviewer.http

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpHeaders
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils
import java.net.ProxySelector
import java.net.URL

/**
 * Component to help with performing
 * REST calls
 */
class Http(
    private val params: YouTrackConnectionSettings,
    private val connectTimeoutInSeconds: Int = 10
) {

    fun get(path : String) : HttpResponse {
        val url = buildUrl(path)
        return get(url)
    }

    fun get(url: URL): HttpResponse {
        val request = HttpGet(url.toURI())
        return execute(request)
    }

    fun post(path : String, payload: HttpEntity) : HttpResponse {
        val url = buildUrl(path)
        return post(url, payload)
    }

    fun post(url: URL, payload: HttpEntity): HttpResponse {
        val request = HttpPost(url.toURI())
        request.entity = payload
        return execute(request)
    }

    fun delete(path : String) {
        val url = buildUrl(path)
        delete(url)
    }

    fun delete(url: URL) {
        val request = HttpDelete(url.toURI())
        execute(request)
    }

    fun <T> download(path : String, handler : (org.apache.http.HttpResponse) -> T) : T {
        val url = buildUrl(path)
        return download(url, handler)
    }

    fun <T> download(url : URL, handler : (org.apache.http.HttpResponse) -> T) : T {
        val request = HttpGet(url.toURI())
        request.config = RequestConfig.custom().setContentCompressionEnabled(false).build()
        return execute(request) { it ->
            handler.invoke(it)
        }
    }

    fun buildUrl(path: String): URL {
        val baseUrlAsString = params.baseUrl.toExternalForm().trimEnd('/')
        val pathCleansed = path.trim().trimStart('/')
        return URL("$baseUrlAsString/$pathCleansed")
    }

    private fun execute(request : HttpUriRequest) : HttpResponse {
        return execute(request) { it ->
            if (it.statusLine.isValid().not()) {
                EntityUtils.consumeQuietly(it.entity)
                HttpResponse(it.statusLine)
            } else {
                HttpResponse(it.statusLine, EntityUtils.toString(it.entity))
            }
        }
    }

    private fun <T> execute(request : HttpUriRequest, handler : (org.apache.http.HttpResponse) -> T) : T {
        return httpClientBuilder.build().use {
            it.execute(request) { response -> handler.invoke(response) }
        }
    }

    private val httpClientBuilder: HttpClientBuilder by lazy {
        val requestConfig = RequestConfig
            .custom()
            .setConnectTimeout(connectTimeoutInSeconds * 1000)
            .setConnectionRequestTimeout(connectTimeoutInSeconds * 1000)
            .build()

        val routePlanner = SystemDefaultRoutePlanner(ProxySelector.getDefault())

        return@lazy HttpClients.custom()
            .setDefaultRequestConfig(requestConfig)
            .setRoutePlanner(routePlanner)
            .setDefaultHeaders(httpHeaders)
    }

    private val httpHeaders: List<Header> by lazy {
        listOf(
            BasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36"),
            BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, sdch"),
            BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4"),
            BasicHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*"),
            BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer ${params.permanentToken}")
        )
    }

}