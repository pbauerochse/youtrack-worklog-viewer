package de.pbauerochse.worklogviewer.details.events

import de.pbauerochse.worklogviewer.timereport.Issue

/**
 * An event asking the application to close an [Issue] details view
 */
data  class CloseIssueDetailsRequestEvent(
    val issue: Issue
)
