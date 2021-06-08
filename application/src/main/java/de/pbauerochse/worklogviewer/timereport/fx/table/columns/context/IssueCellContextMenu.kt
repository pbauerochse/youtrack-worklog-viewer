package de.pbauerochse.worklogviewer.timereport.fx.table.columns.context

import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.fx.dialog.workitem.WorkitemDialogs
import de.pbauerochse.worklogviewer.openInBrowser
import de.pbauerochse.worklogviewer.search.fx.SearchTabModel
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.slf4j.LoggerFactory
import java.time.LocalDate

/**
 * Context menu for an [de.pbauerochse.worklogviewer.fx.components.treetable.columns.IssueLinkColumn]
 */
class IssueCellContextMenu(private val issue: Issue, private val date: LocalDate? = null, showAddForOtherIssueItem : Boolean = true) : ContextMenu() {

    init {
        val openIssueInBrowserMenu = MenuItem(getFormatted("contextmenu.issue.openinyoutrack", issue.humanReadableId))
        openIssueInBrowserMenu.onAction = EventHandler { issue.externalUrl.openInBrowser() }
        items.add(openIssueInBrowserMenu)

        val addWorkItemForIssueMenu = MenuItem(getFormatted("contextmenu.issue.addworkitem", issue.humanReadableId))
        addWorkItemForIssueMenu.onAction = EventHandler { showAddWorkItemToIssueDialog() }
        items.add(addWorkItemForIssueMenu)

        if (showAddForOtherIssueItem) {
            val addWorkItemToAnyIssueMenu = MenuItem(getFormatted("contextmenu.issue.addanyworkitem"))
            addWorkItemToAnyIssueMenu.onAction = EventHandler { showAddWorkItemToAnyIssueDialog() }
            items.add(addWorkItemToAnyIssueMenu)
        }

        addFavourites()
    }

    private fun addFavourites() {
        items.add(SeparatorMenuItem())

        val issueAlreadyMarkedAsFavourite = Bindings.createBooleanBinding({ FavouritesService.isFavourite(issue) }, SearchTabModel.favouriteIssues)

        val markAsFavourite = MenuItem(getFormatted("contextmenu.issue.addfavourite", issue.humanReadableId)).apply {
            visibleProperty().bind(issueAlreadyMarkedAsFavourite.not())
            onAction = EventHandler {
                LOGGER.info("Marking ${issue.humanReadableId} as Favourite")
                FavouritesService.addFavourite(issue)
            }
        }

        val removeFromFavourites = MenuItem(getFormatted("contextmenu.issue.removefavourite", issue.humanReadableId)).apply {
            visibleProperty().bind(issueAlreadyMarkedAsFavourite)
            onAction = EventHandler {
                LOGGER.info("Removing ${issue.humanReadableId} from Favourites")
                FavouritesService.removeFavourite(issue)
            }
        }

        items.addAll(markAsFavourite, removeFromFavourites)
    }

    fun showAddWorkItemToIssueDialog() = WorkitemDialogs.show(scene, date, issue)
    private fun showAddWorkItemToAnyIssueDialog() = WorkitemDialogs.show(scene, date)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueCellContextMenu::class.java)
    }
}
