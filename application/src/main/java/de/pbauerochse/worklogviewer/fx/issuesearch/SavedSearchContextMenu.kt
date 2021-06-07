package de.pbauerochse.worklogviewer.fx.issuesearch

import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import org.slf4j.LoggerFactory

class SavedSearchContextMenu(search: FavouriteSearch) : ContextMenu() {

    init {
        val removeFromFavourites = MenuItem(getFormatted("contextmenu.issue.removefavourite", "\"${search.name}\"")).apply {
            onAction = EventHandler {
                LOGGER.info("Removing FavouriteSearch $search from favourites")
                FavouritesService.removeFavourite(search)
            }
        }
        items.add(removeFromFavourites)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SavedSearchContextMenu::class.java)
    }
}
