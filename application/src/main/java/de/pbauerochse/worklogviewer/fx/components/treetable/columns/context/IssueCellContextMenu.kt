package de.pbauerochse.worklogviewer.fx.components.treetable.columns.context

import de.pbauerochse.worklogviewer.fx.dialog.workitem.WorkitemDialogs
import de.pbauerochse.worklogviewer.openInBrowser
import de.pbauerochse.worklogviewer.report.MinimalIssue
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.settings.favourites.FavouriteIssue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.beans.binding.Bindings
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.concurrent.Callable

/**
 * Context menu for an [de.pbauerochse.worklogviewer.fx.components.treetable.columns.IssueLinkColumn]
 */
class IssueCellContextMenu(private val issue: MinimalIssue, private val date: LocalDate? = null, showAddForOtherIssueItem : Boolean = true) : ContextMenu() {

    init {
        val openIssueInBrowserMenu = MenuItem(getFormatted("contextmenu.issue.openinyoutrack", issue.id))
        openIssueInBrowserMenu.onAction = EventHandler { issue.openInBrowser() }
        items.add(openIssueInBrowserMenu)

        val addWorkItemForIssueMenu = MenuItem(getFormatted("contextmenu.issue.addworkitem", issue.id))
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

        val favouritesModel = SettingsUtil.settingsViewModel.favourites
        val itemAsFavourite = FavouriteIssue(issue)

        val issueAlreadyMarkedAsFavourite = Bindings.createBooleanBinding(Callable<Boolean> { favouritesModel.issues.contains(itemAsFavourite) }, favouritesModel.issues)

        val markAsFavourite = MenuItem(getFormatted("contextmenu.issue.addfavourite", issue.id))
        markAsFavourite.onAction = EventHandler {
            LOGGER.info("Marking ${issue.id} as Favourite")
            favouritesModel.issues.add(itemAsFavourite)
        }
        markAsFavourite.visibleProperty().bind(issueAlreadyMarkedAsFavourite.not())
        items.add(markAsFavourite)

        val removeFromFavourites = MenuItem(getFormatted("contextmenu.issue.removefavourite", issue.id))
        removeFromFavourites.onAction = EventHandler {
            LOGGER.info("Removing ${issue.id} from Favourites")
            favouritesModel.issues.remove(itemAsFavourite)
        }
        removeFromFavourites.visibleProperty().bind(issueAlreadyMarkedAsFavourite)
        items.add(removeFromFavourites)
    }

    fun showAddWorkItemToIssueDialog() = WorkitemDialogs.show(scene, date, issue)
    private fun showAddWorkItemToAnyIssueDialog() = WorkitemDialogs.show(scene, date)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueCellContextMenu::class.java)
    }
}
