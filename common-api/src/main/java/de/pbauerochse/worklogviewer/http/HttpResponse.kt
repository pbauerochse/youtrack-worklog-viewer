package de.pbauerochse.worklogviewer.http

import org.apache.hc.core5.http.message.StatusLine


data class HttpResponse(
    val statusLine: StatusLine,
    val content: String? = null,
    val headers: Map<String, String> = mapOf()
) {
    val isError: Boolean = statusLine.isValid().not()
    val error: String = statusLine.reasonPhrase
}