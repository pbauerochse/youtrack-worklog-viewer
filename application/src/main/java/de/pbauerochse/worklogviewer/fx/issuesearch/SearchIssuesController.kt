package de.pbauerochse.worklogviewer.fx.issuesearch

import de.pbauerochse.worklogviewer.addIfMissing
import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses
import de.pbauerochse.worklogviewer.fx.components.treetable.columns.context.IssueCellContextMenu
import de.pbauerochse.worklogviewer.fx.issuesearch.details.IssueDetailsPanel
import de.pbauerochse.worklogviewer.fx.issuesearch.listview.IssueSearchTreeItem
import de.pbauerochse.worklogviewer.fx.issuesearch.listview.IssueSearchTreeItemCell
import de.pbauerochse.worklogviewer.fx.issuesearch.savedsearch.EditFavouriteSearchDialog
import de.pbauerochse.worklogviewer.fx.issuesearch.task.LoadSingleIssueTask
import de.pbauerochse.worklogviewer.fx.issuesearch.task.SearchIssuesTask
import de.pbauerochse.worklogviewer.fx.tasks.TaskExecutor
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.settings.favourites.FavouriteIssue
import de.pbauerochse.worklogviewer.settings.favourites.FavouriteSearch
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

/**
 * Controller for the view to search for issues
 */
class SearchIssuesController : Initializable {

    @FXML
    private lateinit var queryTextField: TextField

    @FXML
    private lateinit var triggerSearchButton: Button

    @FXML
    private lateinit var saveSearchButton: Button

    @FXML
    private lateinit var issuesView: TreeView<IssueSearchTreeItem>

    @FXML
    private lateinit var issueDetailPanelContainer: BorderPane

    lateinit var taskExecutor: TaskExecutor

    private lateinit var favouriteSearchesTreeItem: TreeItem<IssueSearchTreeItem>
    private lateinit var favouriteIssuesTreeItem: TreeItem<IssueSearchTreeItem>
    private lateinit var searchResultsTreeItem: TreeItem<IssueSearchTreeItem>

    private val lastSearchQueryProperty = SimpleStringProperty()
    private val lastSearchResultProperty = FXCollections.observableArrayList<Issue>()
    private val issueDetailsPanel: IssueDetailsPanel = IssueDetailsPanel()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        issueDetailPanelContainer.center = issueDetailsPanel
        issueDetailPanelContainer.center = BorderPane().apply { center = Label(getFormatted("dialog.issuesearch.noselection")).apply { isWrapText = true } }

        initializeSearchElements()
        initializeTreeView()
        initializeFavourites()

        saveSearchButton.onAction = EventHandler {
            val result = EditFavouriteSearchDialog(FavouriteSearch("", queryTextField.text), queryTextField.scene?.window).showAndWait()
            result.ifPresent { SettingsUtil.settingsViewModel.favourites.searches.addIfMissing(it) }
        }
    }

    private fun initializeSearchElements() {
        triggerSearchButton.disableProperty().bind(queryTextField.textProperty().isEmpty)
        triggerSearchButton.onAction = EventHandler { startNewSearch(queryTextField.text) }
    }

    private fun initializeTreeView() {
        lastSearchResultProperty.addListener(ListChangeListener { updateSearchResultsTreeItem(it.list) })

        favouriteSearchesTreeItem = TreeItem(IssueSearchTreeItem.labelledNoopItem(getFormatted("dialog.issuesearch.groups.favourites.searches")))
        favouriteSearchesTreeItem.isExpanded = true

        favouriteIssuesTreeItem = TreeItem(IssueSearchTreeItem.labelledNoopItem(getFormatted("dialog.issuesearch.groups.favourites.issues")))
        favouriteIssuesTreeItem.isExpanded = true

        searchResultsTreeItem = TreeItem(IssueSearchTreeItem.labelledNoopItem(getFormatted("dialog.issuesearch.groups.searchresult")))
        searchResultsTreeItem.isExpanded = true

        issuesView.cellFactory = IssueSearchTreeItemCell.cellFactory()
        issuesView.isShowRoot = false
        issuesView.isEditable = false
        issuesView.selectionModel.selectedItemProperty().addListener { _, _, selectedItem -> selectedItem?.let { selectIssue(it.value) } }
        issuesView.root = TreeItem<IssueSearchTreeItem>().apply {
            children.addAll(favouriteSearchesTreeItem, favouriteIssuesTreeItem, searchResultsTreeItem)
        }
    }

    private fun initializeFavourites() {
        val favouritesModel = SettingsUtil.settingsViewModel.favourites
        favouritesModel.issues.addListener(ListChangeListener { updateFavouritesTreeItem(it.list) })
        favouritesModel.searches.addListener(ListChangeListener { updateSavedSearches(it.list) })

        updateFavouritesTreeItem(favouritesModel.issues)
        updateSavedSearches(favouritesModel.searches)
    }

    private fun updateSearchResultsTreeItem(issues: List<Issue>) {
        val treeItems = issues.map {
            val styleClasses = mutableSetOf(ComponentStyleClasses.ISSUE_LINK_CELL)
            if (it.resolutionDate != null) {
                styleClasses.add(ComponentStyleClasses.RESOLVED_ISSUE_CELL)
            }

            TreeItem(IssueSearchTreeItem(it.fullTitle, { showIssueDetails(it) }, IssueCellContextMenu(it), styleClasses))
        }
        searchResultsTreeItem.children.setAll(treeItems)
    }

    private fun updateFavouritesTreeItem(favourites: List<FavouriteIssue>) {
        val treeItems = favourites.map {
            val data = IssueSearchTreeItem(it.fullTitle, { loadIssue(it) }, IssueCellContextMenu(it))
            TreeItem(data)
        }
        favouriteIssuesTreeItem.children.setAll(treeItems)
    }

    private fun updateSavedSearches(searches: List<FavouriteSearch>) {
        val treeItems = searches.map {
            val data = IssueSearchTreeItem(it.name, { startNewSearch(it.query) }, SavedSearchContextMenu(it))
            TreeItem(data)
        }
        favouriteSearchesTreeItem.children.setAll(treeItems)
    }

    private fun startNewSearch(query: String?) {
        if (query.isNullOrBlank().not()) {
            lastSearchQueryProperty.value = query
            performSearch(query!!)
        }
    }

    private fun performSearch(query: String) {
        val task = SearchIssuesTask(query, 0, DataSources.activeDataSource!!)
        task.onSucceeded = EventHandler { showSearchResults(it) }
        lastSearchQueryProperty.value = query
        taskExecutor.startTask(task)
    }

    private fun showSearchResults(event: WorkerStateEvent) {
        val task = event.source as SearchIssuesTask
        LOGGER.info("Found ${task.value.size} Issues")
        if (task.isNewSearch) {
            lastSearchResultProperty.setAll(task.value)
        } else {
            lastSearchResultProperty.addAll(task.value)
        }
    }

    private fun loadIssue(favouriteIssue: FavouriteIssue) {
        val task = LoadSingleIssueTask(favouriteIssue.id, DataSources.activeDataSource!!)
        task.onSucceeded = EventHandler { event ->
            val finishedTask = event.source as LoadSingleIssueTask
            finishedTask.value?.let { showIssueDetails(it) }
        }
        taskExecutor.startTask(task)
    }

    private fun selectIssue(item: IssueSearchTreeItem) {
        LOGGER.debug("Selected TreeItem $item")
        item.onSelect.invoke()
    }

    private fun showIssueDetails(issue: Issue) {
        LOGGER.info("Selected issue ${issue.fullTitle}")
        issueDetailsPanel.update(issue)
        issueDetailPanelContainer.center = issueDetailsPanel
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchIssuesController::class.java)
    }

}
