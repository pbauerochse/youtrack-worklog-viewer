package de.pbauerochse.worklogviewer.timereport.fx.table.columns

import de.pbauerochse.worklogviewer.openInBrowser
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.ALL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.GRAND_SUMMARY_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.GROUPING_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.ISSUE_ITEM_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.ISSUE_TITLE_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.RESOLVED
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.SUMMARY_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.columns.context.IssueCellContextMenu
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.IssueReportRow
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.scene.input.MouseButton
import javafx.util.Callback

/**
 * Displays the description and the id of the
 * Issue as a link to the YouTrack issue
 */
internal class IssueLinkColumn : TreeTableColumn<ReportRow, ReportRow>(getFormatted("view.main.issue")) {

    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(col.value.value) }
        cellFactory = Callback { IssueLinkCell() }
        prefWidth = 300.0
        minWidth = 300.0
    }
}

/**
 * Cell that displays the label of the [ReportRow]
 *
 * If it is a [IssueReportRow] it will display a link
 * to the actual youtrack issue
 */
private class IssueLinkCell : TreeTableCell<ReportRow, ReportRow>() {

    init {
        onMouseClicked = EventHandler {
            when {
                it.button == MouseButton.PRIMARY && it.clickCount == 1 -> openIssueLinkInBrowser()
            }
        }
    }

    override fun updateItem(item: ReportRow?, empty: Boolean) {
        super.updateItem(item, empty)

        styleClass.removeAll(ALL)
        tooltip = null
        text = null

        item?.let { reportRow ->
            text = reportRow.label
            tooltip = Tooltip(reportRow.label)

            when {
                reportRow.isGrouping -> handleGrouping()
                reportRow.isIssue -> handleIssue(reportRow as IssueReportRow)
                reportRow.isSummary -> handleSummary()
            }
        }
    }

    private fun handleGrouping() {
        contextMenu = null
        styleClass.add(GROUPING_CELL)
    }

    private fun handleIssue(reportGroup: IssueReportRow) {
        contextMenu = IssueCellContextMenu(reportGroup.issueWithWorkItems.issue)
        styleClass.addAll(ISSUE_ITEM_CELL, ISSUE_TITLE_CELL)
        if (reportGroup.issueWithWorkItems.issue.isResolved) {
            styleClass.add(RESOLVED)
        }
    }

    private fun handleSummary() {
        styleClass.addAll(SUMMARY_CELL, GRAND_SUMMARY_CELL)
    }

    private fun openIssueLinkInBrowser() {
        issueItem?.issueWithWorkItems?.issue?.openInBrowser()
    }

    private val issueItem: IssueReportRow?
        get() = if (isIssueItem) item as IssueReportRow else null

    private val isIssueItem
        get() = item != null && item.isIssue

}
