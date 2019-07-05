package de.pbauerochse.worklogviewer.fx.issuesearch

import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses
import de.pbauerochse.worklogviewer.fx.components.treetable.columns.context.IssueCellContextMenu
import de.pbauerochse.worklogviewer.fx.issuesearch.details.IssueDetailsPanel
import de.pbauerochse.worklogviewer.fx.issuesearch.task.LoadSingleIssueTask
import de.pbauerochse.worklogviewer.fx.issuesearch.task.SearchIssuesTask
import de.pbauerochse.worklogviewer.fx.issuesearch.treeview.IssueSearchTreeColumn
import de.pbauerochse.worklogviewer.fx.tasks.TaskRunnerImpl
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
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
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
    private lateinit var issuesView: TreeTableView<IssueSearchTreeItem>

    @FXML
    private lateinit var issueDetailPanelContainer: BorderPane

    @FXML
    private lateinit var actionsToolbar: ToolBar

    @FXML
    private lateinit var progressIndicator: StackPane

    @FXML
    private lateinit var progressBarContainer: VBox

    private lateinit var taskRunner: TaskRunnerImpl

    private lateinit var favouriteSearchesTreeItem: TreeItem<IssueSearchTreeItem>
    private lateinit var favouriteIssuesTreeItem: TreeItem<IssueSearchTreeItem>
    private lateinit var searchResultsTreeItem: TreeItem<IssueSearchTreeItem>

    private val lastSearchQueryProperty = SimpleStringProperty()
    private val lastSearchResultProperty = FXCollections.observableArrayList<Issue>()
    private val issueDetailsPanel: IssueDetailsPanel = IssueDetailsPanel()

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        taskRunner = TaskRunnerImpl(progressBarContainer, progressIndicator)
        issueDetailPanelContainer.center = issueDetailsPanel
        issueDetailPanelContainer.center = BorderPane().apply { center = Label(getFormatted("dialog.issuesearch.noselection")).apply { isWrapText = true } }

        initializeSearchElements()
        initializeIssueTreeTableView()
        initializeFavourites()

        actionsToolbar.items.addAll(
            Button("<"),
            Button(">")
        )
    }

    private fun initializeSearchElements() {
        triggerSearchButton.onAction = EventHandler { startNewSearch(queryTextField.text) }
        triggerSearchButton.disableProperty().bind(queryTextField.textProperty().isEmpty)
        queryTextField.onKeyPressed = EventHandler {
            if (it.code == KeyCode.ENTER) {
                startNewSearch(queryTextField.text)
            }
        }
    }

    private fun initializeIssueTreeTableView() {
        lastSearchResultProperty.addListener(ListChangeListener { updateSearchResultsTreeItem(it.list) })

        favouriteSearchesTreeItem = TreeItem(IssueSearchTreeItem.labelledNoopItem(getFormatted("dialog.issuesearch.groups.favourites.searches")))
        favouriteSearchesTreeItem.isExpanded = true

        favouriteIssuesTreeItem = TreeItem(IssueSearchTreeItem.labelledNoopItem(getFormatted("dialog.issuesearch.groups.favourites.issues")))
        favouriteIssuesTreeItem.isExpanded = true

        searchResultsTreeItem = TreeItem(IssueSearchTreeItem.labelledNoopItem(getFormatted("dialog.issuesearch.groups.searchresult")))
        searchResultsTreeItem.isExpanded = true

        issuesView.isShowRoot = false
        issuesView.columns.add(IssueSearchTreeColumn())
        issuesView.selectionModel.selectedItemProperty().addListener { _, _, selectedItem -> selectIssue(selectedItem.value) }
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
            val data = IssueSearchTreeItem(it.title, { startNewSearch(it.query) })
            TreeItem(data)
        }
        favouriteSearchesTreeItem.children.setAll(treeItems)
    }

    private fun startNewSearch(query : String?) {
        if (query.isNullOrBlank().not()) {
            lastSearchQueryProperty.value = query
            performSearch(query!!)
        }
    }

    private fun performSearch(query: String) {
        val task = SearchIssuesTask(query, 0, YouTrackConnectorLocator.getActiveConnector()!!)
        task.onSucceeded = EventHandler { showSearchResults(it) }
        lastSearchQueryProperty.value = query
        taskRunner.startTask(task)
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
        val task = LoadSingleIssueTask(favouriteIssue.id, YouTrackConnectorLocator.getActiveConnector()!!)
        task.onSucceeded = EventHandler { event ->
            val finishedTask = event.source as LoadSingleIssueTask
            finishedTask.value?.let { showIssueDetails(it) }
        }
        taskRunner.startTask(task)
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
