package de.pbauerochse.worklogviewer.fx.components.treetable.columns

import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.ALL_WORKLOGVIEWER_CLASSES
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.GROUP_TITLE_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.ISSUE_LINK_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.RESOLVED_ISSUE_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.SUMMARY_CELL
import de.pbauerochse.worklogviewer.fx.components.treetable.columns.context.IssueCellContextMenu
import de.pbauerochse.worklogviewer.openInBrowser
import de.pbauerochse.worklogviewer.report.view.ReportRow
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

        styleClass.removeAll(ALL_WORKLOGVIEWER_CLASSES)
        tooltip = null
        text = null

        item?.let { reportGroup ->
            text = reportGroup.label
            tooltip = Tooltip(reportGroup.label)

            when {
                reportGroup.isGrouping -> handleGrouping()
                reportGroup.isIssue -> handleIssue(reportGroup as IssueReportRow)
                reportGroup.isSummary -> handleSummary()
            }
        }
    }

    private fun handleGrouping() {
        contextMenu = null
        styleClass.add(GROUP_TITLE_CELL)
    }

    private fun handleIssue(reportGroup: IssueReportRow) {
        contextMenu = IssueCellContextMenu(reportGroup.issue)
        styleClass.add(ISSUE_LINK_CELL)
        reportGroup.issue.resolutionDate?.let {
            styleClass.add(RESOLVED_ISSUE_CELL)
        }
    }

    private fun handleSummary() {
        styleClass.add(SUMMARY_CELL)
    }

    private fun openIssueLinkInBrowser() {
        issueItem?.issue?.openInBrowser()
    }

    private val issueItem: IssueReportRow?
        get() = if (isIssueItem) item as IssueReportRow else null

    private val isIssueItem
        get() = item != null && item.isIssue

}
