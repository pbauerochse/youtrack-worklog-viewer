package de.pbauerochse.worklogviewer.search.fx

import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch
import de.pbauerochse.worklogviewer.fx.issuesearch.savedsearch.EditFavouriteSearchDialog
import de.pbauerochse.worklogviewer.fx.issuesearch.task.SearchIssuesTask
import de.pbauerochse.worklogviewer.fx.tasks.TaskExecutor
import javafx.beans.property.SimpleStringProperty
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import org.slf4j.LoggerFactory
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
    lateinit var taskExecutor: TaskExecutor

    private val lastSearchQueryProperty = SimpleStringProperty()

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

    private fun startNewSearch(query: String?) {
        if (query.isNullOrBlank().not()) {
            lastSearchQueryProperty.value = query
            performSearch(query!!)
        }
    }

    private fun performSearch(query: String) {
        val task = SearchIssuesTask(query, 0, MAX_SEARCH_RESULTS, DataSources.activeDataSource!!)
        task.onSucceeded = EventHandler { showSearchResults(it) }
        lastSearchQueryProperty.value = query
        taskExecutor.startTask(task)
    }

    private fun showSearchResults(event: WorkerStateEvent) {
        val task = event.source as SearchIssuesTask
        LOGGER.info("Found ${task.value.size} Issues")
        if (task.isNewSearch) {
            SearchTabModel.searchResults.setAll(task.value)
        } else {
            SearchTabModel.searchResults.addAll(task.value)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchTabController::class.java)
        private const val MAX_SEARCH_RESULTS = 50
    }

}
