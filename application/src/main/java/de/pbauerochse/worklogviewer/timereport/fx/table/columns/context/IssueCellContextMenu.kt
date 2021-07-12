package de.pbauerochse.worklogviewer.timereport.fx.table.columns.context

import de.pbauerochse.worklogviewer.timereport.Issue
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import java.time.LocalDate

/**
 * Context menu for an [de.pbauerochse.worklogviewer.fx.components.treetable.columns.IssueLinkColumn]
 */
class IssueCellContextMenu(issue: Issue, date: LocalDate? = null, showAddForOtherIssueItem: Boolean = true) : ContextMenu() {

    private val addWorkItemMenuItem: AddWorkItemToIssueMenuItem = AddWorkItemToIssueMenuItem(issue, date) { scene }

    init {
        items.addAll(
            OpenIssueInBrowserMenuItem(issue),
            OpenIssueInDetailsPaneMenuItem(issue),
            addWorkItemMenuItem
        )

        if (showAddForOtherIssueItem) {
            items.add(AddWorkItemToOtherIssueMenuItem(date) { scene })
        }

        items.addAll(
            SeparatorMenuItem(),
            ToggleFavouriteMenuItem(issue)
        )
    }

    internal fun showAddWorkItemToIssueDialog() {
        addWorkItemMenuItem.showAddWorkItemToIssueDialog()
    }
}
