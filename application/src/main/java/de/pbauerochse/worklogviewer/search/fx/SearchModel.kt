package de.pbauerochse.worklogviewer.search.fx

import de.pbauerochse.worklogviewer.timereport.Issue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * ViewModel for the whole SearchTab section,
 * including the [de.pbauerochse.worklogviewer.search.fx.details.IssueDetailsPanel] and the favourites section
 */
object SearchModel {

    /**
     * Contains the results yielded by the most recent search
     */
    val searchResults: ObservableList<Issue> = FXCollections.observableArrayList()

}