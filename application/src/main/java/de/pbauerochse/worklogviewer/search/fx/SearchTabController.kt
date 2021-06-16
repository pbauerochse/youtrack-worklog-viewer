package de.pbauerochse.worklogviewer.search.fx

import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch
import de.pbauerochse.worklogviewer.fx.issuesearch.savedsearch.EditFavouriteSearchDialog
import de.pbauerochse.worklogviewer.search.fx.details.IssueDetailsModel
import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
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

    lateinit var searchResultView: Parent
    lateinit var placeholderContent: Label

    private val shouldShowPlaceholder = Bindings.isEmpty(SearchModel.searchResults).and(Bindings.isEmpty(IssueDetailsModel.issuesForDetailsPanel))

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        triggerSearchButton.apply {
            disableProperty().bind(queryTextField.textProperty().isEmpty)
            onAction = EventHandler { startNewSearch(queryTextField.text) }
        }
        saveSearchButton.onAction = EventHandler {
            val result = EditFavouriteSearchDialog(FavouriteSearch("", queryTextField.text), queryTextField.scene?.window).showAndWait()
            result.ifPresent { FavouritesService.addFavourite(it) }
        }

        placeholderContent.visibleProperty().bind(shouldShowPlaceholder)
        searchResultView.visibleProperty().bind(shouldShowPlaceholder.not())
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
