package de.pbauerochse.worklogviewer.fx.issuesearch

import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator
import de.pbauerochse.worklogviewer.fx.issuesearch.details.IssueDetailsPanel
import de.pbauerochse.worklogviewer.fx.issuesearch.task.SearchIssuesTask
import de.pbauerochse.worklogviewer.fx.issuesearch.treeview.IssueSearchTreeColumn
import de.pbauerochse.worklogviewer.fx.tasks.TaskRunnerImpl
import de.pbauerochse.worklogviewer.report.Issue
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

        lastSearchResultProperty.addListener(ListChangeListener { updateIssuesView(it.list) })

        initializeSearchElements()
        initializeIssueTreeTableView()

        actionsToolbar.items.addAll(
            Button("<"),
            Button(">")
        )
    }

    private fun initializeSearchElements() {
        triggerSearchButton.onAction = EventHandler { startNewSearch() }
        triggerSearchButton.disableProperty().bind(queryTextField.textProperty().isEmpty)
        queryTextField.onKeyPressed = EventHandler {
            if (it.code == KeyCode.ENTER) {
                startNewSearch()
            }
        }
    }

    private fun initializeIssueTreeTableView() {
        favouriteSearchesTreeItem = TreeItem(NamedIssueList(getFormatted("dialog.issuesearch.groups.favourites.searches")))
        favouriteSearchesTreeItem.isExpanded = true

        favouriteIssuesTreeItem = TreeItem(NamedIssueList(getFormatted("dialog.issuesearch.groups.favourites.issues")))
        favouriteIssuesTreeItem.isExpanded = true

        searchResultsTreeItem = TreeItem(NamedIssueList(getFormatted("dialog.issuesearch.groups.searchresult")))
        searchResultsTreeItem.isExpanded = true

        issuesView.isShowRoot = false
        issuesView.columns.add(IssueSearchTreeColumn())
        issuesView.selectionModel.selectedItemProperty().addListener { _, _, selectedItem -> selectIssue(selectedItem.value) }
        issuesView.root = TreeItem<IssueSearchTreeItem>().apply {
            children.addAll(favouriteSearchesTreeItem, favouriteIssuesTreeItem, searchResultsTreeItem)
        }
    }

    private fun updateIssuesView(issues: List<Issue>) {
        searchResultsTreeItem.children.clear()
        searchResultsTreeItem.children.addAll(issues.map { TreeItem(IssueTreeItem(it) as IssueSearchTreeItem) }.toList())
    }

    private fun startNewSearch() {
        if (queryTextField.text.isNullOrBlank().not()) {
            lastSearchQueryProperty.value = queryTextField.text
            performSearch(queryTextField.text)
        }
    }

    private fun performSearch(query: String) {
        val task = SearchIssuesTask(query, 0, YouTrackConnectorLocator.getActiveConnector()!!)
        task.onSucceeded = EventHandler { updateIssueList(it) }
        lastSearchQueryProperty.value = query
        taskRunner.startTask(task)
    }

    private fun updateIssueList(event: WorkerStateEvent) {
        val task = event.source as SearchIssuesTask
        LOGGER.info("Found ${task.value.size} Issues")
        if (task.isNewSearch) {
            lastSearchResultProperty.setAll(task.value)
        } else {
            lastSearchResultProperty.addAll(task.value)
        }
    }

    private fun selectIssue(item: IssueSearchTreeItem) {
        item.issue?.let {
            LOGGER.info("Selected issue ${it.fullTitle}")
            issueDetailsPanel.update(it)
            issueDetailPanelContainer.center = issueDetailsPanel
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchIssuesController::class.java)
    }

}
