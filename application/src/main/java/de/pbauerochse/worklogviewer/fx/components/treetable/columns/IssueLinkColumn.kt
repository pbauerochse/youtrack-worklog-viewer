package de.pbauerochse.worklogviewer.fx.components.treetable.columns

import de.pbauerochse.worklogviewer.WorklogViewer
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.ALL_WORKLOGVIEWER_CLASSES
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.GROUP_TITLE_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.ISSUE_LINK_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.RESOLVED_ISSUE_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.SUMMARY_CELL
import de.pbauerochse.worklogviewer.getYouTrackLink
import de.pbauerochse.worklogviewer.report.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.IssueReportRow
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback
import org.slf4j.LoggerFactory

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
        onMouseClicked = EventHandler { openIssueLinkInBrowser() }
    }

    override fun updateItem(item: ReportRow?, empty: Boolean) {
        super.updateItem(item, empty)

        styleClass.removeAll(ALL_WORKLOGVIEWER_CLASSES)
        tooltip = null
        text = null

        item?.let { reportGroup ->
            text = reportGroup.label
            tooltip = Tooltip(reportGroup.label)

            if (reportGroup.isGrouping) {
                styleClass.add(GROUP_TITLE_CELL)
            }

            if (reportGroup.isIssue) {
                val issue = (reportGroup as IssueReportRow).issue
                styleClass.add(ISSUE_LINK_CELL)

                issue.resolutionDate?.let {
                    styleClass.add(RESOLVED_ISSUE_CELL)
                }
            }

            if (reportGroup.isSummary) {
                styleClass.add(SUMMARY_CELL)
            }
        }
    }

    private fun openIssueLinkInBrowser() {
        if (item != null && item.isIssue) {
            val issueTableRowModel = item as IssueReportRow
            LOGGER.debug("Clicked cell ${issueTableRowModel.issue.id}")
            Platform.runLater { WorklogViewer.getInstance().hostServices.showDocument(issueTableRowModel.issue.getYouTrackLink().toExternalForm()) }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueLinkCell::class.java)
    }

}
