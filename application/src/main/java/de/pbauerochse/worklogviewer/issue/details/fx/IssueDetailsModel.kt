package de.pbauerochse.worklogviewer.issue.details.fx

import de.pbauerochse.worklogviewer.issue.details.FetchWorkItemsForIssueTask
import de.pbauerochse.worklogviewer.tasks.Tasks
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.WorkItem
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler

object IssueDetailsModel {

    /**
     * Contains the most recently selected [Issue]
     * so that it shows up in the [de.pbauerochse.worklogviewer.search.fx.details.IssueDetailsPanel]
     */
    val selectedIssueForDetails: ObjectProperty<Issue> = SimpleObjectProperty()

    /**
     * The [WorkItem]s for the selected [Issue]. May not be set at
     * the same time, the `selectedIssueForDetails` is set
     */
    val selectedIssueWorkItems: ObservableList<WorkItem> = FXCollections.observableArrayList()

    init {
        selectedIssueForDetails.addListener { _, _, newValue ->
            selectedIssueWorkItems.clear()
            newValue?.let {
                val task = FetchWorkItemsForIssueTask(it).apply {
                    onSucceeded = EventHandler {
                        val issueWithWorkItems = this.value
                        selectedIssueWorkItems.addAll(issueWithWorkItems.workItems)
                    }
                }
                Tasks.startBackgroundTask(task)
            }
        }
    }
}