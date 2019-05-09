package de.pbauerochse.worklogviewer.fx.components.treetable.columns

import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.ALL_WORKLOGVIEWER_CLASSES
import de.pbauerochse.worklogviewer.fx.components.treetable.data.IssueTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.data.TimeReportRowModel
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.ReportGroup
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback
import org.slf4j.LoggerFactory

/**
 * Displays the description and the id of the
 * Issue as a link to the YouTrack issue
 */
internal class IssueLinkColumn : TreeTableColumn<ReportGroup, ReportGroup>(getFormatted("view.main.issue")) {

    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(col.value.value) }
        cellFactory = Callback { IssueLinkCell() }
        prefWidth = 300.0
        minWidth = 300.0
    }
}

/**
 * Cell that displays the label of the [TimeReportRowModel]
 *
 * If it is a [IssueTreeTableRow] it will display a link
 * to the actual youtrack issue
 */
private class IssueLinkCell : TreeTableCell<ReportGroup, ReportGroup>() {

    init {
//        onMouseClicked = EventHandler { openIssueLinkInBrowser() }
    }

    override fun updateItem(item: ReportGroup?, empty: Boolean) {
        super.updateItem(item, empty)

        styleClass.removeAll(ALL_WORKLOGVIEWER_CLASSES)
        tooltip = null
        text = null

        item?.let {
            text = it.label
            tooltip = Tooltip(it.label)
            //TODO

//            if (it.isGroupByRow) {
//                text = it.getLabel()
//                tooltip = Tooltip(text)
//                styleClass.add(GROUP_TITLE_CELL)
//            }
//
//            if (it.isIssueRow) {
//                val issue = (it as IssueTreeTableRow).issue
//
//                text = it.getLabel()
//                tooltip = Tooltip(text)
//                styleClass.add(ISSUE_LINK_CELL)
//
//                issue.resolutionDate?.let {
//                    styleClass.add(RESOLVED_ISSUE_CELL)
//                }
//            }
//
//            if (it.isSummaryRow) {
//                text = it.getLabel()
//                styleClass.add(SUMMARY_CELL)
//            }
        }
    }

//    private fun openIssueLinkInBrowser() {
//        if (item != null && item.isIssueRow) {
//            val issueTableRowModel = item as IssueTreeTableRow
//            LOGGER.debug("Clicked cell ${issueTableRowModel.issue.id}")
//            Platform.runLater { WorklogViewer.getInstance().hostServices.showDocument(issueTableRowModel.issue.getYouTrackLink().toExternalForm()) }
//        }
//    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueLinkCell::class.java)
    }

}
