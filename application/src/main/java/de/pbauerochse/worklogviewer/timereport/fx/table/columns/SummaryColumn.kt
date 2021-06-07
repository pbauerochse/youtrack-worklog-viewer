package de.pbauerochse.worklogviewer.timereport.fx.table.columns

import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.GROUP_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.SUMMARY_CELL
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

        styleClass.removeAll(ComponentStyleClasses.ALL_WORKLOGVIEWER_CLASSES)
        tooltip = null
        text = null

        if (!empty && item != null) {
            val timeInMinutes = item.totalDurationInMinutes
            if (timeInMinutes > 0) {
                text = formatMinutes(timeInMinutes)
            }

            when {
                item.isSummary -> styleClass.add(SUMMARY_CELL)
                item.isIssue -> styleClass.add(SUMMARY_CELL)
                item.isGrouping -> styleClass.add(GROUP_CELL)
            }
        }
    }
}
