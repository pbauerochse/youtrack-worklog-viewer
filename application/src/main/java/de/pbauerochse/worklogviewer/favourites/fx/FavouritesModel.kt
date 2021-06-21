package de.pbauerochse.worklogviewer.favourites.fx

import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.events.Subscribe
import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.favourites.events.FavouriteAddedEvent
import de.pbauerochse.worklogviewer.favourites.events.FavouriteRemovedEvent
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
        EventBus.subscribe(this)
    }

    @Subscribe
    fun onFavouritesAdded(event: FavouriteAddedEvent) {
        event.addedIssue?.let { favouriteIssues.add(it) }
        event.addedSearch?.let { favouriteSearches.add(it) }
    }

    @Subscribe
    fun onFavouritesRemoved(event: FavouriteRemovedEvent) {
        event.removedIssue?.let { favouriteIssues.remove(it) }
        event.removedSearch?.let { favouriteSearches.remove(it) }
    }
}