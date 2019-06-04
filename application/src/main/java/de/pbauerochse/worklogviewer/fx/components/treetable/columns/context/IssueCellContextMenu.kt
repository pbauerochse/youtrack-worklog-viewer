package de.pbauerochse.worklogviewer.fx.components.treetable.columns.context

import de.pbauerochse.worklogviewer.fx.dialog.workitem.WorkitemDialogs
import de.pbauerochse.worklogviewer.openInBrowser
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import java.time.LocalDate

/**
 * Context menu for an [de.pbauerochse.worklogviewer.fx.components.treetable.columns.IssueLinkColumn]
 */
class IssueCellContextMenu(private val issue: Issue, private val date: LocalDate? = null) : ContextMenu() {

    init {
        val openIssueInBrowserMenu = MenuItem(getFormatted("contextmenu.issue.openinyoutrack", issue.id))
        openIssueInBrowserMenu.onAction = EventHandler { issue.openInBrowser() }
        items.add(openIssueInBrowserMenu)

        val addWorkItemForIssueMenu = MenuItem(getFormatted("contextmenu.issue.addworkitem", issue.id))
        addWorkItemForIssueMenu.onAction = EventHandler { showAddWorkItemToIssueDialog() }
        items.add(addWorkItemForIssueMenu)

        val addWorkItemToAnyIssueMenu = MenuItem(getFormatted("contextmenu.issue.addanyworkitem"))
        addWorkItemToAnyIssueMenu.onAction = EventHandler { showAddWorkItemToAnyIssueDialog() }
        items.add(addWorkItemToAnyIssueMenu)
    }

    fun showAddWorkItemToIssueDialog() = WorkitemDialogs.show(scene, date, issue)
    private fun showAddWorkItemToAnyIssueDialog() = WorkitemDialogs.show(scene, date)

}
