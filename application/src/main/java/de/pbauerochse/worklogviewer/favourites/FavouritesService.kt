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

/**
 * Service for managing [Issue]s and Searches marked as favourite
 */
object FavouritesService {

    private val favouriteIssues: MutableList<FavouriteIssue> by lazy {
        val storedIssues = SettingsUtil.settings.favourites.issues
        val loadDetailsTask = LoadFavouriteIssuesDetailsTask(storedIssues)

        val issuesFuture = Tasks.startBackgroundTask(loadDetailsTask)
        val results = issuesFuture.get()

        return@lazy results.toMutableList()
    }

    private val favouriteSearches = mutableListOf<FavouriteSearch>()

    val issues: List<FavouriteIssue>
        get() = favouriteIssues.toList()

    val searches: List<FavouriteSearch>
        get() = favouriteSearches.toList()

    fun addFavourite(issue: FavouriteIssue) {
        if (!favouriteIssues.contains(issue)) {
            favouriteIssues.add(issue)
            EventBus.publish(FavouriteAddedEvent.forIssue(issue))
        }
    }

    fun addFavourite(search: FavouriteSearch) {
        if (!favouriteSearches.contains(search)) {
            favouriteSearches.add(search)
            EventBus.publish(FavouriteAddedEvent.forSearch(search))
        }
    }

    fun isFavourite(issue: Issue): Boolean {
        return favouriteIssues.any { it.issue.id == issue.id }
    }

    fun removeFavourite(issue: Issue) {
        val favourite = favouriteIssues.find { it.issue.id == issue.id }

        if (favourite != null) {
            favouriteIssues.remove(favourite)
            EventBus.publish(FavouriteRemovedEvent.forIssue(favourite))
        }
    }

    fun removeFavourite(search: FavouriteSearch) {
        if (favouriteSearches.remove(search)) {
            EventBus.publish(FavouriteRemovedEvent.forSearch(search))
        }
    }

}