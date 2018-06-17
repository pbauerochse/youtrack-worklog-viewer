package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses
import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.GROUP_CELL
import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.SUMMARY_CELL
import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback

/**
 * Displays the total amount of time spent
 * for each Issue, as well as a total time
 * spent within the timerange row
 */
internal class SummaryColumn : TreeTableColumn<TreeTableRowModel, TreeTableRowModel>(FormattingUtil.getFormatted("view.main.summary")) {
    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(col.value.value) }
        cellFactory = Callback { _ -> SummaryCell() }
        prefWidth = 120.0
    }
}

class SummaryCell : TreeTableCell<TreeTableRowModel, TreeTableRowModel>() {

    override fun updateItem(item: TreeTableRowModel?, empty: Boolean) {
        super.updateItem(item, empty)

        styleClass.removeAll(CellStyleClasses.ALL_WORKLOGVIEWER_CLASSES)
        tooltip = null
        text = null

        if (!empty && item != null) {
            val timeInMinutes = item.getTotalTimeSpent()
            if (timeInMinutes > 0) {
                text = formatMinutes(timeInMinutes)
            }

            when {
                item.isSummaryRow -> styleClass.add(SUMMARY_CELL)
                item.isIssueRow -> styleClass.add(SUMMARY_CELL)
                item.isGroupByRow -> styleClass.add(GROUP_CELL)
            }
        }
    }
}
