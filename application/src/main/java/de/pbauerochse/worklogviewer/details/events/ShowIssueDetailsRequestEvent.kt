package de.pbauerochse.worklogviewer.details.events

import de.pbauerochse.worklogviewer.timereport.Issue

/**
 * Triggers the passed [Issue] to be shown in the details pane
 */
data class ShowIssueDetailsRequestEvent(
    val issue: Issue
)