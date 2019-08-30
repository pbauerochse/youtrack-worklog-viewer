package de.pbauerochse.worklogviewer.fx.issuesearch

import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.settings.favourites.FavouriteSearch
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import org.slf4j.LoggerFactory

class SavedSearchContextMenu(search: FavouriteSearch) : ContextMenu() {

    init {
        val removeFromFavourites = MenuItem(getFormatted("contextmenu.issue.removefavourite", "\"${search.name}\""))
        removeFromFavourites.onAction = EventHandler {
            LOGGER.info("Removing FavouriteSearch $search from favourites")
            val favouritesModel = SettingsUtil.settingsViewModel.favourites
            favouritesModel.searches.remove(search)
        }
        items.add(removeFromFavourites)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SavedSearchContextMenu::class.java)
    }
}
