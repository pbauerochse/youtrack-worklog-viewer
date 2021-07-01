package de.pbauerochse.worklogviewer.timereport.fx.table.columns

import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.ALL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.GRAND_SUMMARY_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.GROUPING_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.ISSUE_ITEM_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.SUMMARY_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.TIME_SPENT_CELL
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback

/**
 * Displays the total amount of time spent
 * for each Issue, as well as a total time
 * spent within the timerange row
 */
internal class SummaryColumn : TreeTableColumn<ReportRow, ReportRow>(getFormatted("view.main.summary")) {
    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(col.value.value) }
        cellFactory = Callback { SummaryCell() }
        prefWidth = 120.0
    }
}

class SummaryCell : TreeTableCell<ReportRow, ReportRow>() {

    override fun updateItem(item: ReportRow?, empty: Boolean) {
        super.updateItem(item, empty)

        styleClass.removeAll(ALL)

        tooltip = null
        text = null

        if (!empty && item != null) {
            styleClass.addAll(SUMMARY_CELL, TIME_SPENT_CELL)
            val timeInMinutes = item.totalDurationInMinutes
            if (timeInMinutes > 0) {
                text = formatMinutes(timeInMinutes)
            }

            when {
                item.isIssue -> styleClass.add(ISSUE_ITEM_CELL)
                item.isGrouping -> styleClass.add(GROUPING_CELL)
                item.isSummary -> styleClass.add(GRAND_SUMMARY_CELL)
            }
        }
    }
}
