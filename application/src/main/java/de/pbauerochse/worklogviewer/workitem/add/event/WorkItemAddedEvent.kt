package de.pbauerochse.worklogviewer.workitem.add.event

import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.WorkItem

data class WorkItemAddedEvent(
    val issue: Issue,
    val addedWorkItem: WorkItem
)