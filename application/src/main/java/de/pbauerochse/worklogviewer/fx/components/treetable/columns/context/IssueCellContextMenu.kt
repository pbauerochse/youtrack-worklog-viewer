package de.pbauerochse.worklogviewer.fx.components.treetable.columns.context

import de.pbauerochse.worklogviewer.fx.AddWorkItemController
import de.pbauerochse.worklogviewer.fx.dialog.Dialog
import de.pbauerochse.worklogviewer.openInBrowser
import de.pbauerochse.worklogviewer.plugins.dialog.DialogSpecification
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.RESOURCE_BUNDLE
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
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

    fun showAddWorkItemToIssueDialog() = openAddWorkItemDialog(issue, date)
    private fun showAddWorkItemToAnyIssueDialog() = openAddWorkItemDialog(null, date)

    private fun openAddWorkItemDialog(issue: Issue?, date: LocalDate?) {
        val loader = FXMLLoader(IssueCellContextMenu::class.java.getResource("/fx/views/add-workitem.fxml"), RESOURCE_BUNDLE)
        val root = loader.load<Parent>()
        val controller = loader.getController<AddWorkItemController>()
        controller.issueProperty.set(issue)
        controller.dateProperty.set(date)

        Dialog(scene).openDialog(root, DialogSpecification(getFormatted("dialog.addworkitem.title")))
    }

}