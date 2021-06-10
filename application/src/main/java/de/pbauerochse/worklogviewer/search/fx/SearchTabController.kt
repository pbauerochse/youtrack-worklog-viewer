package de.pbauerochse.worklogviewer.search.fx

import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch
import de.pbauerochse.worklogviewer.fx.issuesearch.savedsearch.EditFavouriteSearchDialog
import javafx.event.EventHandler
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import java.net.URL
import java.util.*

/**
 * Controller for the view to search for issues
 * and manage favourites
 */
class SearchTabController : Initializable {

    lateinit var queryTextField: TextField
    lateinit var triggerSearchButton: Button
    lateinit var saveSearchButton: Button

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        initializeSearchElements()

        saveSearchButton.onAction = EventHandler {
            val result = EditFavouriteSearchDialog(FavouriteSearch("", queryTextField.text), queryTextField.scene?.window).showAndWait()
            result.ifPresent { FavouritesService.addFavourite(it) }
        }
    }

    private fun initializeSearchElements() {
        triggerSearchButton.disableProperty().bind(queryTextField.textProperty().isEmpty)
        triggerSearchButton.onAction = EventHandler { startNewSearch(queryTextField.text) }
    }

    private fun startNewSearch(query: String) {
        if (query.isNotBlank()) {
            Search.issues(query, 0, MAX_SEARCH_RESULTS)
        }
    }

    companion object {
        private const val MAX_SEARCH_RESULTS = 50
    }

}
