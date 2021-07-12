package de.pbauerochse.worklogviewer.favourites.fx

import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch
import de.pbauerochse.worklogviewer.timereport.Issue
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * Model for the Favourites View
 */
object FavouritesModel {

    /**
     * Contains the [Issue]s the user has marked as [FavouriteIssue]
     */
    val favouriteIssues: ObservableList<FavouriteIssue> = FXCollections.observableArrayList()

    /**
     * Contains the users saved searches
     */
    val favouriteSearches: ObservableList<FavouriteSearch> = FXCollections.observableArrayList()


    init {
        Bindings.bindContent(favouriteIssues, FavouritesService.issues)
        Bindings.bindContent(favouriteSearches, FavouritesService.searches)
    }

}