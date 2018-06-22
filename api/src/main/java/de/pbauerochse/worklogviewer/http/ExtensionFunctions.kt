package de.pbauerochse.worklogviewer.http

import org.apache.http.HttpStatus
import org.apache.http.StatusLine

/**
 * Extension functions for the http package
 */

fun StatusLine.isValid() : Boolean =
    statusCode in (HttpStatus.SC_OK until HttpStatus.SC_MULTIPLE_CHOICES)
