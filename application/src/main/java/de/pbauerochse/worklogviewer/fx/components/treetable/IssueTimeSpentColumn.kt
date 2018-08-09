package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.ALL_WORKLOGVIEWER_CLASSES
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.GROUP_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.HIGHLIGHT_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.SUMMARY_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.TIMESPENT_CELL
import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses.TODAY_HIGHLIGHT_CELL
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatDate
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
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
internal class IssueTimeSpentColumn : TreeTableColumn<TreeTableRowModel, TimeSpentColumnData>() {

    private val columnDateProperty = SimpleObjectProperty<LocalDate>()

    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(TimeSpentColumnData(columnDateProperty, col.value.value)) }
        cellFactory = Callback { _ -> TimeSpentColumn() }
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
    val rowModel: TreeTableRowModel
)

private class TimeSpentColumn : TreeTableCell<TreeTableRowModel, TimeSpentColumnData>() {

    override fun updateItem(item: TimeSpentColumnData?, empty: Boolean) {
        super.updateItem(item, empty)

        text = null
        tooltip = null
        styleClass.removeAll(ALL_WORKLOGVIEWER_CLASSES)

        if (!empty && item != null) {
            val date = item.dateProperty.get()

            when {
                item.rowModel.isGroupByRow -> handleGroupBy(date, item.rowModel as GroupedIssuesTreeTableRow)
                item.rowModel.isIssueRow -> handleIssue(date, item.rowModel as IssueTreeTableRow)
                item.rowModel.isSummaryRow -> handleSummary(date, item.rowModel as SummaryTreeTableRow)
            }

            when {
                isHighlighted(date) -> styleClass.add(HIGHLIGHT_CELL)
                isToday(date) -> styleClass.add(TODAY_HIGHLIGHT_CELL)
            }
        }
    }

    private fun handleGroupBy(date: LocalDate, row: GroupedIssuesTreeTableRow) {
        val totalTimeSpentInMinutes = row.totalTimeSpentOn(date)
        if (totalTimeSpentInMinutes > 0) {
            text = formatMinutes(totalTimeSpentInMinutes)
        }
        styleClass.add(GROUP_CELL)
    }

    private fun handleIssue(date: LocalDate, row: IssueTreeTableRow) {
        val timeSpentInMinutes = row.issue.getTimeSpentOn(date)
        if (timeSpentInMinutes > 0) {
            text = formatMinutes(timeSpentInMinutes)
            tooltip = Tooltip("${tableColumn.text} - ${row.issue.id} : $text")
            styleClass.add(TIMESPENT_CELL)
        }
    }

    private fun handleSummary(date: LocalDate, row: SummaryTreeTableRow) {
        val totalTimeSpentInMinutes = row.getTotalTimeSpentOn(date)
        if (totalTimeSpentInMinutes > 0) {
            text = formatMinutes(totalTimeSpentInMinutes)
            styleClass.add(SUMMARY_CELL)
        }
    }

    private fun isToday(date: LocalDate): Boolean = date.isEqual(LocalDate.now())

    private fun isHighlighted(date: LocalDate): Boolean = SettingsUtil.settings.highlightState.isSet(date.dayOfWeek)
}
