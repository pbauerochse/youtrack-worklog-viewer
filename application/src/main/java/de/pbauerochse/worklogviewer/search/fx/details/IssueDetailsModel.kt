package de.pbauerochse.worklogviewer.search.fx.details

import de.pbauerochse.worklogviewer.timereport.Issue
import javafx.collections.FXCollections

object IssueDetailsModel {

    val issuesForDetailsPanel = FXCollections.observableArrayList<Issue>()

    fun showDetails(issue: Issue) {
        if (issuesForDetailsPanel.none { it.id == issue.id }) {
            issuesForDetailsPanel.add(issue)
        }
    }


}