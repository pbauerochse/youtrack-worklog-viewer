package de.pbauerochse.worklogviewer.fx.components.treetable.columns

import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.ALL_WORKLOGVIEWER_CLASSES
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.GROUP_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.HIGHLIGHT_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.SUMMARY_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.TIMESPENT_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.TODAY_HIGHLIGHT_CELL
import de.pbauerochse.worklogviewer.report.view.ReportRow
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatDate
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
import de.pbauerochse.worklogviewer.view.GroupReportRow
import de.pbauerochse.worklogviewer.view.IssueReportRow
import de.pbauerochse.worklogviewer.view.SummaryReportRow
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback
import java.time.LocalDate

/**
 * Displays the total spent time at a given
 * day for the Issue
 */
internal class IssueTimeSpentColumn : TreeTableColumn<ReportRow, TimeSpentColumnData>() {

    private val columnDateProperty = SimpleObjectProperty<LocalDate>()

    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(TimeSpentColumnData(columnDateProperty, col.value.value)) }
        cellFactory = Callback { TimeSpentColumn() }
    }

    fun update(date: LocalDate) {
        text = formatDate(date)
        columnDateProperty.set(date)

        // setting using css classes leads
        // to very strange column width
        // behaviour
        prefWidth = if (isCollapsed(date)) COLLAPSED_WIDTH else REGULAR_WIDTH
    }

    companion object {
        private const val COLLAPSED_WIDTH = 20.0
        private const val REGULAR_WIDTH = 100.0
        internal fun isCollapsed(date: LocalDate): Boolean = SettingsUtil.settings.collapseState.isSet(date.dayOfWeek)
    }

}

/**
 * Bundles the TreeTableRowModel together
 * with the date for this column
 */
data class TimeSpentColumnData(
    val dateProperty: ReadOnlyObjectProperty<LocalDate>,
    val reportRow: ReportRow
)

private class TimeSpentColumn : TreeTableCell<ReportRow, TimeSpentColumnData>() {

    override fun updateItem(item: TimeSpentColumnData?, empty: Boolean) {
        super.updateItem(item, empty)

        text = null
        tooltip = null
        styleClass.removeAll(ALL_WORKLOGVIEWER_CLASSES)

        if (!empty && item != null) {
            val date = item.dateProperty.get()

            when {
                item.reportRow.isGrouping -> handleGroupBy(date, item.reportRow as GroupReportRow)
                item.reportRow.isIssue -> handleIssue(date, item.reportRow as IssueReportRow)
                item.reportRow.isSummary -> handleSummary(date, item.reportRow as SummaryReportRow)
            }

            when {
                isHighlighted(date) -> styleClass.add(HIGHLIGHT_CELL)
                isToday(date) -> styleClass.add(TODAY_HIGHLIGHT_CELL)
            }
        }
    }

    private fun handleGroupBy(date: LocalDate, row: GroupReportRow) {
        val totalTimeSpentInMinutes = row.getDurationInMinutes(date)
        if (totalTimeSpentInMinutes > 0) {
            text = formatMinutes(totalTimeSpentInMinutes)
        }
        styleClass.add(GROUP_CELL)
    }

    private fun handleIssue(date: LocalDate, row: IssueReportRow) {
        val timeSpentInMinutes = row.issue.getTimeInMinutesSpentOn(date)
        if (timeSpentInMinutes > 0) {
            text = formatMinutes(timeSpentInMinutes)
            tooltip = Tooltip("${tableColumn.text} - ${row.issue.id} : $text")
            styleClass.add(TIMESPENT_CELL)
        }
    }

    private fun handleSummary(date: LocalDate, row: SummaryReportRow) {
        val totalTimeSpentInMinutes = row.getDurationInMinutes(date)
        if (totalTimeSpentInMinutes > 0) {
            text = formatMinutes(totalTimeSpentInMinutes)
            styleClass.add(SUMMARY_CELL)
        }
    }

    private fun isToday(date: LocalDate): Boolean = date.isEqual(LocalDate.now())

    private fun isHighlighted(date: LocalDate): Boolean = SettingsUtil.settings.highlightState.isSet(date.dayOfWeek)
}
