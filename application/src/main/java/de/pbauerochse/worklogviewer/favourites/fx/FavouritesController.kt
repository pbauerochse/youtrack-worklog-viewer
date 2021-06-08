package de.pbauerochse.worklogviewer.favourites.fx

import de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses
import de.pbauerochse.worklogviewer.fx.issuesearch.SavedSearchContextMenu
import de.pbauerochse.worklogviewer.search.fx.SearchTabModel
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.fx.table.columns.context.IssueCellContextMenu
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.collections.ListChangeListener
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.MouseEvent.MOUSE_PRESSED
import javafx.scene.text.Text
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

/**
 * FX Controller for managing the users [de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue]s
 * and [de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch]es.
 */
class FavouritesController : Initializable {

    lateinit var favouritesTreeView: TreeView<IssueSearchTreeItem>

    private lateinit var favouriteSearchesTreeItem: TreeItem<IssueSearchTreeItem>
    private lateinit var favouriteIssuesTreeItem: TreeItem<IssueSearchTreeItem>
    private lateinit var searchResultsTreeItem: TreeItem<IssueSearchTreeItem>

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        // FavouriteSearches
        favouriteSearchesTreeItem = TreeItem(IssueSearchTreeItem.labelledNoopItem(FormattingUtil.getFormatted("dialog.issuesearch.groups.favourites.searches"))).apply {
            isExpanded = true
        }

        // FavouriteIssues
        favouriteIssuesTreeItem = TreeItem(IssueSearchTreeItem.labelledNoopItem(FormattingUtil.getFormatted("dialog.issuesearch.groups.favourites.issues"))).apply {
            isExpanded = true
        }


        // SearchResults (will be removed later)
        searchResultsTreeItem = TreeItem(IssueSearchTreeItem.labelledNoopItem(FormattingUtil.getFormatted("dialog.issuesearch.groups.searchresult"))).apply {
            isExpanded = true
        }

        // Tree View

        favouritesTreeView.addEventFilter(MOUSE_PRESSED) { event ->
            // prevent selection on right click, but still show context menu
            // see https://stackoverflow.com/a/61779016
            if (event.isSecondaryButtonDown) {
                val node = event.target as Node
                val treeCell: TreeCell<*>? = when (node) {
                    is TreeCell<*> -> node
                    is Text -> node.parent as TreeCell<*>
                    else -> null
                }

                treeCell?.let {
                    it.contextMenu?.show(treeCell, 0.0, 0.0)
                    event.consume()
                }
            }
        }
        favouritesTreeView.apply {
            cellFactory = IssueSearchTreeItemCell.cellFactory()
            isShowRoot = false
            isEditable = false
            selectionModel.selectedItemProperty().addListener { _, _, selectedItem -> selectedItem?.let { selectIssue(it.value) } }
            root = TreeItem<IssueSearchTreeItem>().apply {
                children.addAll(favouriteSearchesTreeItem, favouriteIssuesTreeItem, searchResultsTreeItem)
            }
        }

        initializeFavourites()
        initializeSearchResults()
    }

    private fun initializeFavourites() {
        SearchTabModel.favouriteIssues.addListener(ListChangeListener {
            updateFavouriteIssuesTreeItem(it.list)
        })

        SearchTabModel.favouriteSearches.addListener(ListChangeListener {
            updateSavedSearchesTreeItem(it.list)

        })

        updateSavedSearchesTreeItem(SearchTabModel.favouriteSearches)
        updateFavouriteIssuesTreeItem(SearchTabModel.favouriteIssues)
    }

    private fun initializeSearchResults() {
        SearchTabModel.searchResults.addListener(ListChangeListener { updateSearchResultsTreeItem(it.list) })
    }

    private fun updateFavouriteIssuesTreeItem(issues: List<FavouriteIssue>) {
        favouriteIssuesTreeItem.children.setAll(issueTreeItems(issues.map { it.issue }))
    }

    private fun updateSavedSearchesTreeItem(searches: List<FavouriteSearch>) {
        favouriteSearchesTreeItem.children.setAll(searchesTreeItems(searches))
    }

    private fun updateSearchResultsTreeItem(issues: List<Issue>) {
        searchResultsTreeItem.children.setAll(issueTreeItems(issues))
    }

    private fun issueTreeItems(issues: List<Issue>): List<TreeItem<IssueSearchTreeItem>> {
        return issues.map {
            val styleClasses = mutableSetOf(ComponentStyleClasses.ISSUE_LINK_CELL)
            if (it.resolutionDate != null) {
                styleClasses.add(ComponentStyleClasses.RESOLVED_ISSUE_CELL)
            }

            TreeItem(IssueSearchTreeItem(it.fullTitle, { showIssueDetails(it) }, IssueCellContextMenu(it), styleClasses))
        }
    }

    private fun searchesTreeItems(searches: List<FavouriteSearch>): List<TreeItem<IssueSearchTreeItem>> {
        return searches.map {
            val data = IssueSearchTreeItem(it.name, { startNewSearch(it.query) }, SavedSearchContextMenu(it))
            TreeItem(data)
        }
    }

    private fun selectIssue(item: IssueSearchTreeItem) {
        LOGGER.debug("Selected TreeItem $item")
        item.onSelect.invoke()
    }

    private fun startNewSearch(query: String) {
        TODO("Not yet implemented")
    }

    private fun showIssueDetails(issue: Issue) {
        LOGGER.info("Selected issue ${issue.fullTitle}")
        SearchTabModel.selectedIssueForDetails.set(issue)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FavouritesController::class.java)
    }
}