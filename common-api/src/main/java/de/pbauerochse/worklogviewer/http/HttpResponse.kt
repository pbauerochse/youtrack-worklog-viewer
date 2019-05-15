package de.pbauerochse.worklogviewer.http

import org.apache.http.StatusLine

data class HttpResponse(
    val statusLine: StatusLine,
    val content: String? = null,
    val headers: Map<String, String> = mapOf()
) {
    val isError: Boolean = statusLine.isValid().not()
    val error: String = statusLine.reasonPhrase
}