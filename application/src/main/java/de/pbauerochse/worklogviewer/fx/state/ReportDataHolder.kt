package de.pbauerochse.worklogviewer.fx.state

import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.events.Subscribe
import de.pbauerochse.worklogviewer.issue.details.FetchWorkItemsForIssueTask
import de.pbauerochse.worklogviewer.tasks.Tasks
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.TimeReport
import de.pbauerochse.worklogviewer.workitem.add.event.WorkItemAddedEvent
import javafx.beans.property.SimpleObjectProperty

/**
 * For static access to the current [TimeReport]
 * being displayed
 */
object ReportDataHolder {

    val currentTimeReportProperty = SimpleObjectProperty<TimeReport?>(null)

    init {
        EventBus.subscribe(this)
    }

    @Subscribe
    fun workItemCreated(event: WorkItemAddedEvent) {
        currentTimeReportProperty.value?.let { timeReport ->
            if (timeReport.reportParameters.timerange.contains(event.addedWorkItem.workDateAtLocalZone.toLocalDate())) {
                // only update report, when the added WorkItem is within the currently displayed range
                val fetchWorkItemsTask = FetchWorkItemsForIssueTask(event.issue, timeReport.reportParameters.timerange).apply {
                    setOnSucceeded { updateTimeReport((it.source as FetchWorkItemsForIssueTask).value, timeReport) }
                }
                Tasks.startBackgroundTask(fetchWorkItemsTask)
            }
        }
    }

    /**
     * adds the given [IssueWithWorkItems] into the old [TimeReport]
     * by creating a copy with the [IssueWithWorkItems] applied
     * to the copy
     */
    private fun updateTimeReport(issueWithWorkItems: IssueWithWorkItems, oldTimeReport: TimeReport) {
        val issueList = oldTimeReport.issues.toMutableList()

        // remove existing issue and add the current item
        issueList.removeIf { it.issue.id == issueWithWorkItems.issue.id }
        issueList.add(issueWithWorkItems)

        val updatedTimeReport = TimeReport(oldTimeReport.reportParameters, issueList)
        currentTimeReportProperty.set(updatedTimeReport)
    }
}