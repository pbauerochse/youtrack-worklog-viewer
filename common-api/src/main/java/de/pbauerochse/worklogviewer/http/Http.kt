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
        return execute(request) { response ->
            val mappedHeaders = mutableMapOf<String, String>()
            response.allHeaders.map { mappedHeaders[it.name] = it.value }

            if (response.statusLine.isValid().not()) {
                EntityUtils.consumeQuietly(response.entity)
                HttpResponse(
                    statusLine = response.statusLine,
                    headers = mappedHeaders
                )
            } else {
                HttpResponse(
                    statusLine = response.statusLine,
                    content = EntityUtils.toString(response.entity),
                    headers = mappedHeaders
                )
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
