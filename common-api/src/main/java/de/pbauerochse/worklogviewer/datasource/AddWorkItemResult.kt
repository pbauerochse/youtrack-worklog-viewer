package de.pbauerochse.worklogviewer.datasource

import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.WorkItem

data class AddWorkItemResult(
    val issue: Issue,
    val addedWorkItem: WorkItem
)