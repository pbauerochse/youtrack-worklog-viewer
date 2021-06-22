package de.pbauerochse.worklogviewer.timereport.fx.table.columns.context

import de.pbauerochse.worklogviewer.details.events.ShowIssueDetailsRequestEvent
import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.favourites.fx.FavouritesModel
import de.pbauerochse.worklogviewer.openInBrowser
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.workitem.add.fx.AddWorkItemDialog
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
class IssueCellContextMenu(private val issue: Issue, private val date: LocalDate? = null, showAddForOtherIssueItem: Boolean = true) : ContextMenu() {

    init {
        val openIssueInBrowserMenu = MenuItem(getFormatted("contextmenu.issue.openinyoutrack", issue.humanReadableId))
        openIssueInBrowserMenu.onAction = EventHandler { issue.externalUrl.openInBrowser() }
        items.add(openIssueInBrowserMenu)

        val openIssueInDetailsPaneMenu = MenuItem(getFormatted("contextmenu.issue.openindetails", issue.humanReadableId))
        openIssueInDetailsPaneMenu.onAction = EventHandler { EventBus.publish(ShowIssueDetailsRequestEvent(issue)) }
        items.add(openIssueInDetailsPaneMenu)

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

        val issueAlreadyMarkedAsFavourite = Bindings.createBooleanBinding({ FavouritesService.isFavourite(issue) }, FavouritesModel.favouriteIssues)

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

    fun showAddWorkItemToIssueDialog() = AddWorkItemDialog.show(scene, date, issue)
    private fun showAddWorkItemToAnyIssueDialog() = AddWorkItemDialog.show(scene, date)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueCellContextMenu::class.java)
    }
}
