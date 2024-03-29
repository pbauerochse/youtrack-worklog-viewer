package de.pbauerochse.worklogviewer.search.fx

import de.pbauerochse.worklogviewer.timereport.Issue
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * ViewModel for the whole SearchTab section,
 * including the [de.pbauerochse.worklogviewer.search.fx.details.IssueDetailsPanel] and the favourites section
 */
object SearchModel {

    /**
     * The search term used for the search
     */
    val searchTerm: StringProperty = SimpleStringProperty()

    /**
     * Contains the results yielded by the most recent search
     */
    val searchResults: ObservableList<Issue> = FXCollections.observableArrayList()

}