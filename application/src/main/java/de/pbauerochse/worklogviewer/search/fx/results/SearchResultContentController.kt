package de.pbauerochse.worklogviewer.search.fx.results

import de.pbauerochse.worklogviewer.search.fx.SearchModel
import de.pbauerochse.worklogviewer.search.fx.details.IssueDetailsModel
import de.pbauerochse.worklogviewer.search.fx.details.IssueDetailsTab
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.beans.binding.Bindings.createStringBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ListChangeListener
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.input.MouseEvent
import javafx.util.Callback
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

class SearchResultContentController : Initializable {

    lateinit var searchTermLabel: Label
    lateinit var searchResultCountLabel: Label

    lateinit var searchContentPane: TabPane
    lateinit var searchResultsTab: Tab
    lateinit var searchResultsListView: ListView<Issue>

    private val searchTriggeredBinding = SearchModel.searchTerm.isNotEmpty

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        searchTermLabel.apply {
            textProperty().bind(createStringBinding({ getFormatted("search.results.term", SearchModel.searchTerm.value) }, SearchModel.searchTerm))
            visibleProperty().bind(searchTriggeredBinding)
        }
        searchResultCountLabel.apply {
            textProperty().bind(createStringBinding({ getFormatted("search.results.count", SearchModel.searchResults.size) }, SearchModel.searchResults))
            visibleProperty().bind(searchTriggeredBinding)
        }
        searchResultsTab.disableProperty().bind(searchTriggeredBinding.not())

        searchResultsListView.apply {
            itemsProperty().bind(SimpleObjectProperty(SearchModel.searchResults))
            addEventFilter(MouseEvent.MOUSE_PRESSED) {
                // prevent selection when context menu requested
                if (it.isSecondaryButtonDown) {
                    it.consume()
                }
            }
            cellFactory = Callback { SearchResultListViewItem() }
            selectionModel.selectedItemProperty().addListener { _, oldValue, newValue ->
                LOGGER.info("Selection $oldValue, $newValue")
                newValue?.let { IssueDetailsModel.showDetails(it) }
            }
        }

        IssueDetailsModel.issuesForDetailsPanel.addListener(ListChangeListener {
            while (it.next()) {
                it.addedSubList.forEach { addedIssue -> showTab(addedIssue) }
            }
        })
        SearchModel.searchResults.addListener(ListChangeListener { searchContentPane.selectionModel.select(searchResultsTab) })
    }

    private fun showTab(issue: Issue) {
        val tabs = searchContentPane.tabs
        LOGGER.info("Current Tabs ${tabs.joinToString { it.text }}")
        val tab = tabs.find { it.text == issue.humanReadableId } ?: createNewTab(issue)
        searchContentPane.selectionModel.select(tab)
    }

    private fun createNewTab(issue: Issue): Tab {
        LOGGER.debug("Adding Tab for $issue")

        val tab = IssueDetailsTab(issue).apply {
            setOnCloseRequest { IssueDetailsModel.issuesForDetailsPanel.remove(issue) }
        }
        searchContentPane.tabs.add(tab)
        return tab
    }


    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchResultContentController::class.java)
    }
}