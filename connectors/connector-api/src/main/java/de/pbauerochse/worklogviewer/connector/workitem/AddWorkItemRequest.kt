package de.pbauerochse.worklogviewer.connector.workitem

import java.time.LocalDate

/**
 * Request to the [de.pbauerochse.worklogviewer.connector.YouTrackConnector]
 * to add a workitem to a specific issue
 */
data class AddWorkItemRequest(
    val issueId : String,
    val date: LocalDate,
    val durationInMinutes : Long,
    val description : String? = null
)