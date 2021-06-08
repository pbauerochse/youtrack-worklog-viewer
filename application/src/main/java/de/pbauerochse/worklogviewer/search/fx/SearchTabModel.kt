package de.pbauerochse.worklogviewer.search.fx

import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.events.Subscribe
import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.favourites.events.FavouriteAddedEvent
import de.pbauerochse.worklogviewer.favourites.events.FavouriteRemovedEvent
import de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch
import de.pbauerochse.worklogviewer.issue.details.FetchWorkItemsForIssueTask
import de.pbauerochse.worklogviewer.tasks.Tasks
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.WorkItem
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler

/**
 * ViewModel for the whole SearchTab section,
 * including the [de.pbauerochse.worklogviewer.search.fx.details.IssueDetailsPanel] and the favourites section
 */
object SearchTabModel {

    /**
     * Contains the results yielded by the most recent search
     */
    val searchResults: ObservableList<Issue> = FXCollections.observableArrayList()

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

    /**
     * Contains the [Issue]s the user has marked as [FavouriteIssue]
     */
    val favouriteIssues: ObservableList<FavouriteIssue> = FXCollections.observableArrayList()

    /**
     * Contains the users saved searches
     */
    val favouriteSearches: ObservableList<FavouriteSearch> = FXCollections.observableArrayList()

    init {
        favouriteIssues.setAll(FavouritesService.issues)
        favouriteSearches.setAll(FavouritesService.searches)
        EventBus.subscribe(this)

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