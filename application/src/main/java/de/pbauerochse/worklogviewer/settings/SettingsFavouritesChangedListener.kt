package de.pbauerochse.worklogviewer.settings

import de.pbauerochse.worklogviewer.events.Subscribe
import de.pbauerochse.worklogviewer.favourites.events.FavouriteAddedEvent
import de.pbauerochse.worklogviewer.favourites.events.FavouriteRemovedEvent
import de.pbauerochse.worklogviewer.favourites.settings.PersistedFavouriteIssue
import de.pbauerochse.worklogviewer.favourites.settings.PersistedFavouriteSearch
import org.slf4j.LoggerFactory

object SettingsFavouritesChangedListener {

    private val logger = LoggerFactory.getLogger(SettingsFavouritesChangedListener::class.java)

    @Subscribe
    fun onFavouritesAdded(event: FavouriteAddedEvent) {
        logger.debug("Handling $event")
        val settings = SettingsUtil.settings
        event.addedSearch?.let { settings.favourites.searches.add(PersistedFavouriteSearch(it.name, it.query)) }
        event.addedIssue?.let { settings.favourites.issues.add(PersistedFavouriteIssue(it.issue.humanReadableId, it.issue.title)) }
    }

    @Subscribe
    fun onFavouritesRemoved(event: FavouriteRemovedEvent) {
        logger.debug("Handling $event")
        val settings = SettingsUtil.settings
        event.addedSearch?.let { settings.favourites.searches.removeIf { search -> it.name == search.name && it.query == search.query } }
        event.addedIssue?.let { settings.favourites.issues.removeIf { issue -> issue.id == it.issue.humanReadableId } }
    }

}