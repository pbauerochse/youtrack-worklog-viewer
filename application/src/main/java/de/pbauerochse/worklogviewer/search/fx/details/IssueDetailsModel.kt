package de.pbauerochse.worklogviewer.search.fx.details

import de.pbauerochse.worklogviewer.timereport.Issue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.slf4j.LoggerFactory

object IssueDetailsModel {

    private val LOGGER = LoggerFactory.getLogger(IssueDetailsPane::class.java)

    val issuesForDetailsPanel: ObservableList<Issue> = FXCollections.observableArrayList()

    fun showDetails(issue: Issue) {
        if (issuesForDetailsPanel.none { it.id == issue.id }) {
            LOGGER.debug("Adding $issue to show in the details pane")
            issuesForDetailsPanel.add(issue)
        }
    }

    fun remove(issue: Issue): Boolean {
        LOGGER.debug("Removing $issue from details pane")
        return issuesForDetailsPanel.removeIf { it.id == issue.id }
    }
}