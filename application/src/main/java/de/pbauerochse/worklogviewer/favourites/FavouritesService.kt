package de.pbauerochse.worklogviewer.favourites

import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.favourites.events.FavouriteAddedEvent
import de.pbauerochse.worklogviewer.favourites.events.FavouriteRemovedEvent
import de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue
import de.pbauerochse.worklogviewer.favourites.issue.LoadFavouriteIssuesDetailsTask
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.tasks.Tasks
import de.pbauerochse.worklogviewer.timereport.Issue
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * Service for managing [Issue]s and Searches marked as favourite
 */
object FavouritesService {

    val issues: ObservableList<FavouriteIssue> = FXCollections.observableArrayList()
    val searches: ObservableList<FavouriteSearch> = FXCollections.observableArrayList()

    init {
        loadFavouriteIssues()
        loadFavouriteSearches()
    }

    private fun loadFavouriteIssues() {
        val storedIssues = SettingsUtil.settings.favourites.issues
        val loadDetailsTask = LoadFavouriteIssuesDetailsTask(storedIssues).apply {
            setOnSucceeded { issues.setAll(this.value) }
        }
        Tasks.startBackgroundTask(loadDetailsTask)
    }

    private fun loadFavouriteSearches() {
        searches.setAll(
            SettingsUtil.settings.favourites.searches
                .map { FavouriteSearch(it.name, it.query) }
                .toMutableList()
        )
    }

    fun addFavourite(issue: Issue) {
        if (!isFavourite(issue)) {
            val favourite = FavouriteIssue(issue)
            issues.add(favourite)
            EventBus.publish(FavouriteAddedEvent.forIssue(favourite))
        }
    }

    fun addFavourite(search: FavouriteSearch) {
        if (!searches.contains(search)) {
            searches.add(search)
            EventBus.publish(FavouriteAddedEvent.forSearch(search))
        }
    }

    fun isFavourite(issue: Issue): Boolean {
        return issues.any { it.issue.id == issue.id }
    }

    fun removeFavourite(issue: Issue) {
        val favourite = issues.find { it.issue.id == issue.id }

        if (favourite != null) {
            issues.remove(favourite)
            EventBus.publish(FavouriteRemovedEvent.forIssue(favourite, issues, searches))
        }
    }

    fun removeFavourite(search: FavouriteSearch) {
        if (searches.remove(search)) {
            EventBus.publish(FavouriteRemovedEvent.forSearch(search, issues, searches))
        }
    }

}