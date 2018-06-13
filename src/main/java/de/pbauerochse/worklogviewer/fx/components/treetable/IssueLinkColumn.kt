package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.WorklogViewer
import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.*
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback
import org.slf4j.LoggerFactory

/**
 * Displays the description and the id of the
 * Issue as a link to the YouTrack issue
 */
internal class IssueLinkColumn : TreeTableColumn<TreeTableRowModel, TreeTableRowModel>(getFormatted("view.main.issue")) {

    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(col.value.value) }
        cellFactory = Callback { _ -> IssueLinkCell() }
    }
}

/**
 * Cell that displays the label of the [TreeTableRowModel]
 *
 * If it is a [IssueTreeTableRow] it will display a link
 * to the actual youtrack issue
 */
private class IssueLinkCell : TreeTableCell<TreeTableRowModel, TreeTableRowModel>() {

    init {
        onMouseClicked = EventHandler { _ -> openIssueLinkInBrowser() }
    }

    override fun updateItem(item: TreeTableRowModel?, empty: Boolean) {
        super.updateItem(item, empty)

        alignment = Pos.CENTER_LEFT
        styleClass.removeAll(ALL_WORKLOGVIEWER_CLASSES)
        tooltip = null
        text = null

        item?.let {
            if (it.isGroupByRow) {
                LOGGER.debug("Showing GroupBy Item $it")
                text = it.getLabel()
                tooltip = Tooltip(text)
                styleClass.add(GROUP_COLUMN_OR_CELL_CSS_CLASS)
            }

            if (it.isIssueRow) {
                LOGGER.debug("Showing Issue Item $it")
                text = it.getLabel()
                tooltip = Tooltip(text)
                styleClass.add(ISSUE_CELL_CSS_CLASS)
            }

            if (it.isSummaryRow) {
                LOGGER.debug("Showing Summary Item $it")
                text = it.getLabel()
                styleClass.add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS)
                alignment = Pos.CENTER_RIGHT
            }
        }
    }

    private fun openIssueLinkInBrowser() {
        if (item != null && item.isIssueRow) {
            val issueTableRowModel = item as IssueTreeTableRow
            LOGGER.debug("Clicked cell ${issueTableRowModel.issue.issueId}")
            Platform.runLater { WorklogViewer.getInstance().hostServices.showDocument(issueTableRowModel.issue.getYoutrackLink()) }
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueLinkCell::class.java)
    }

}
