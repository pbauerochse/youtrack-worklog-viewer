package de.pbauerochse.worklogviewer.search.fx.details

import de.pbauerochse.worklogviewer.timereport.Issue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

object IssueDetailsModel {

    val issuesForDetailsPanel: ObservableList<Issue> = FXCollections.observableArrayList<Issue>()

    fun showDetails(issue: Issue) {
        if (issuesForDetailsPanel.none { it.id == issue.id }) {
            issuesForDetailsPanel.add(issue)
        }
    }


}