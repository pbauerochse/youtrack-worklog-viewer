package de.pbauerochse.worklogviewer.http

import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.message.StatusLine

/**
 * Extension functions for the http package
 */
fun StatusLine.isValid(): Boolean =
    statusCode in (HttpStatus.SC_OK until HttpStatus.SC_MULTIPLE_CHOICES)
