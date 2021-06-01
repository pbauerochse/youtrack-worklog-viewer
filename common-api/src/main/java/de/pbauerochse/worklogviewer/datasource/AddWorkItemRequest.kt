package de.pbauerochse.worklogviewer.datasource

import de.pbauerochse.worklogviewer.report.WorkItemType
import java.time.LocalDate

/**
 * Request to the [de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource]
 * to add a workitem to a specific issue
 */
data class AddWorkItemRequest(
    val issueId : String,
    val date: LocalDate,
    val durationInMinutes : Long,
    val workItemType: WorkItemType?,
    val description : String? = null
)