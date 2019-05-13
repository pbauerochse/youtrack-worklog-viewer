package de.pbauerochse.worklogviewer.http

import org.apache.http.HttpEntity
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.*
import org.apache.http.util.EntityUtils
import java.net.URL

/**
 * Component to help with performing
 * REST calls
 */
class Http(private val params: HttpParams) {

    fun get(path: String): HttpResponse {
        val url = params.buildUrl(path)
        println("GET $url")
        return get(url)
    }

    fun get(url: URL): HttpResponse {
        val request = HttpGet(url.toURI())
        return execute(request)
    }

    fun post(path: String, payload: HttpEntity): HttpResponse {
        val url = params.buildUrl(path)
        return post(url, payload)
    }

    fun post(url: URL, payload: HttpEntity): HttpResponse {
        val request = HttpPost(url.toURI())
        request.entity = payload
        return execute(request)
    }

    fun delete(path: String) {
        val url = params.buildUrl(path)
        delete(url)
    }

    fun delete(url: URL) {
        val request = HttpDelete(url.toURI())
        execute(request)
    }

    fun <T> download(path: String, handler: (org.apache.http.HttpResponse) -> T): T {
        val url = params.buildUrl(path)
        return download(url, handler)
    }

    fun <T> download(url: URL, handler: (org.apache.http.HttpResponse) -> T): T {
        val request = HttpGet(url.toURI())
        request.config = RequestConfig.custom().setContentCompressionEnabled(false).build()
        return execute(request) {
            handler.invoke(it)
        }
    }

    private fun execute(request: HttpUriRequest): HttpResponse {
        return execute(request) {
            if (it.statusLine.isValid().not()) {
                EntityUtils.consumeQuietly(it.entity)
                HttpResponse(it.statusLine)
            } else {
                HttpResponse(it.statusLine, EntityUtils.toString(it.entity))
            }
        }
    }

    private fun <T> execute(request: HttpUriRequest, handler: (org.apache.http.HttpResponse) -> T): T {
        return params.httpClientBuilder().build().use { client ->
            client.execute(request) { response ->
                (response as CloseableHttpResponse).use {
                    handler.invoke(it)
                }
            }
        }
    }



}