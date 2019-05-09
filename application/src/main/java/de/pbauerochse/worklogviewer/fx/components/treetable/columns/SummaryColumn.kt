package de.pbauerochse.worklogviewer.fx.components.treetable.columns

import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.ReportGroup
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback

/**
 * Displays the total amount of time spent
 * for each Issue, as well as a total time
 * spent within the timerange row
 */
internal class SummaryColumn : TreeTableColumn<ReportGroup, ReportGroup>(getFormatted("view.main.summary")) {
    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(col.value.value) }
        cellFactory = Callback { SummaryCell() }
        prefWidth = 120.0
    }
}

class SummaryCell : TreeTableCell<ReportGroup, ReportGroup>() {

    override fun updateItem(item: ReportGroup?, empty: Boolean) {
        super.updateItem(item, empty)

        styleClass.removeAll(ComponentStyleClasses.ALL_WORKLOGVIEWER_CLASSES)
        tooltip = null
        text = null

        if (!empty && item != null) {
            val timeInMinutes = item.totalDurationInMinutes
            if (timeInMinutes > 0) {
                text = formatMinutes(timeInMinutes)
            }
//          TODO
//            when {
//                item.isSummaryRow -> styleClass.add(SUMMARY_CELL)
//                item.isIssueRow -> styleClass.add(SUMMARY_CELL)
//                item.isGroupByRow -> styleClass.add(GROUP_CELL)
//            }
        }
    }
}
