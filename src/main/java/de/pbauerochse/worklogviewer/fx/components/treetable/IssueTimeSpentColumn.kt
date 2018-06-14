package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.ALL_WORKLOGVIEWER_CLASSES
import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.GROUP_CELL
import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.HIGHLIGHT_CELL
import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.SUMMARY_CELL
import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.TIMESPENT_CELL
import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.TODAY_HIGHLIGHT_CELL
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatDate
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback
import org.slf4j.LoggerFactory
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
        LOGGER.debug("Showing for $item")

        text = null
        tooltip = null
        styleClass.removeAll(ALL_WORKLOGVIEWER_CLASSES)

        if (!empty && item != null) {
            val date = item.dateProperty.get()

            if (item.rowModel.isGroupByRow) {
                styleClass.add(GROUP_CELL)
            }

            if (item.rowModel.isIssueRow) {
                val issueRowModel = item.rowModel as IssueTreeTableRow
                val timeSpentInMinutes = issueRowModel.issue.getTimeSpentOn(date)
                if (timeSpentInMinutes > 0) {
                    text = formatMinutes(timeSpentInMinutes)
                    tooltip = Tooltip("${tableColumn.text} - ${issueRowModel.issue.issueId} : $text")
                    styleClass.add(TIMESPENT_CELL)
                }
            }

            if (item.rowModel.isSummaryRow) {
                text = "SUMMARY $date"
                styleClass.add(SUMMARY_CELL)
            }

            when {
                isHighlighted(date) -> styleClass.add(HIGHLIGHT_CELL)
                isToday(date) -> styleClass.add(TODAY_HIGHLIGHT_CELL)
            }
        }
    }

    private fun isToday(date: LocalDate): Boolean = date.isEqual(LocalDate.now())

    private fun isHighlighted(date: LocalDate): Boolean = SettingsUtil.settings.highlightState.isSet(date.dayOfWeek)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TimeSpentColumn::class.java)
    }
}
