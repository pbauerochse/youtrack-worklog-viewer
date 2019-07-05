package de.pbauerochse.worklogviewer.report

/**
 * The minimal information required to load
 * the [Issue] from YouTrack again
 */
interface MinimalIssue {
    val id: String
    val summary: String
}