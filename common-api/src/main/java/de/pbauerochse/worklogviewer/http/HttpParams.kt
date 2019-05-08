package de.pbauerochse.worklogviewer.http

import org.apache.http.impl.client.HttpClientBuilder
import java.net.URL

/**
 *
 */
interface HttpParams {
    val connectTimeoutInSeconds: Int
    val youtrackBaseUrl : URL

    fun httpClientBuilder() : HttpClientBuilder

    fun buildUrl(path: String): URL {
        val baseUrlAsString = youtrackBaseUrl.toExternalForm().trimEnd('/')
        val pathCleansed = path.trim().trimStart('/')
        return URL("$baseUrlAsString/$pathCleansed")
    }
}