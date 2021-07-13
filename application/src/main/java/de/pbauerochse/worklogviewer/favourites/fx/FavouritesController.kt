package de.pbauerochse.worklogviewer.favourites.fx

import de.pbauerochse.worklogviewer.details.events.ShowIssueDetailsRequestEvent
import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.favourites.fx.FavouritesStyleClasses.FAVOURITE_ISSUE_CELL
import de.pbauerochse.worklogviewer.favourites.fx.FavouritesStyleClasses.FAVOURITE_SEARCH_CELL
import de.pbauerochse.worklogviewer.favourites.fx.FavouritesStyleClasses.RESOLVED_ISSUE_CELL
import de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch
import de.pbauerochse.worklogviewer.fx.issuesearch.SavedSearchContextMenu
import de.pbauerochse.worklogviewer.search.fx.Search
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.fx.table.columns.context.IssueCellContextMenu
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
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
import java.time.LocalDate
import java.util.*

/**
 * FX Controller for managing the users [de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue]s
 * and [de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch]es.
 */
class FavouritesController : Initializable {

    lateinit var favouritesTreeView: TreeView<FavouriteItem>

    private lateinit var favouriteSearchesTreeItem: TreeItem<FavouriteItem>
    private lateinit var favouriteIssuesTreeItem: TreeItem<FavouriteItem>

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        // FavouriteSearches
        favouriteSearchesTreeItem = TreeItem(FavouriteItem.labeledCategoryHeaderItem(getFormatted("dialog.issuesearch.groups.favourites.searches"))).apply {
            isExpanded = true

        }

        // FavouriteIssues
        favouriteIssuesTreeItem = TreeItem(FavouriteItem.labeledCategoryHeaderItem(getFormatted("dialog.issuesearch.groups.favourites.issues"))).apply {
            isExpanded = true
        }

        // Tree View

        favouritesTreeView.addEventFilter(MOUSE_PRESSED) { event ->
            // prevent selection on right click, but still show context menu
            // see https://stackoverflow.com/a/61779016
            if (event.isSecondaryButtonDown) {
                val treeCell: TreeCell<*>? = when (val node = event.target as Node) {
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
            root = TreeItem<FavouriteItem>().apply {
                children.addAll(favouriteSearchesTreeItem, favouriteIssuesTreeItem)
            }
        }

        initializeFavourites()
    }

    private fun initializeFavourites() {
        FavouritesModel.favouriteIssues.addListener(ListChangeListener {
            updateFavouriteIssuesTreeItem(it.list)
        })

        FavouritesModel.favouriteSearches.addListener(ListChangeListener {
            updateSavedSearchesTreeItem(it.list)
        })

        updateSavedSearchesTreeItem(FavouritesModel.favouriteSearches)
        updateFavouriteIssuesTreeItem(FavouritesModel.favouriteIssues)
    }

    private fun updateFavouriteIssuesTreeItem(issues: List<FavouriteIssue>) {
        favouriteIssuesTreeItem.children.setAll(issueTreeItems(issues.map { it.issue }.sorted()))
    }

    private fun updateSavedSearchesTreeItem(searches: List<FavouriteSearch>) {
        favouriteSearchesTreeItem.children.setAll(searchesTreeItems(searches.sortedBy { it.name }))
    }

    private fun issueTreeItems(issues: List<Issue>): List<TreeItem<FavouriteItem>> {
        return issues.map { issue ->
            val styleClasses = listOfNotNull(FAVOURITE_ISSUE_CELL, RESOLVED_ISSUE_CELL.takeIf { issue.isResolved })
            TreeItem(FavouriteItem(
                label = issue.fullTitle,
                onSelect = { showIssueDetails(issue) },
                contextMenu = IssueCellContextMenu(issue, LocalDate.now()),
                styleClasses = styleClasses
            ))
        }
    }

    private fun searchesTreeItems(searches: List<FavouriteSearch>): List<TreeItem<FavouriteItem>> {
        return searches.map {
            val data = FavouriteItem(
                label = it.name,
                onSelect = { Search.issues(it.query) },
                contextMenu = SavedSearchContextMenu(it),
                styleClasses = setOf(FAVOURITE_SEARCH_CELL)
            )
            TreeItem(data)
        }
    }

    private fun selectIssue(item: FavouriteItem) {
        LOGGER.debug("Selected TreeItem $item")
        item.onSelect?.invoke()
    }

    private fun showIssueDetails(issue: Issue) {
        LOGGER.info("Selected issue ${issue.fullTitle}")
        EventBus.publish(ShowIssueDetailsRequestEvent(issue))
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FavouritesController::class.java)
    }
}