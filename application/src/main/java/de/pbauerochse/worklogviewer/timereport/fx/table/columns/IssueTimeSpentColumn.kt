package de.pbauerochse.worklogviewer.timereport.fx.table.columns

import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.ALL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.GRAND_SUMMARY_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.GROUPING_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.HIGHLIGHT
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.ISSUE_ITEM_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.SUMMARY_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.TIME_SPENT_CELL
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeViewStyleClasses.TODAY
import de.pbauerochse.worklogviewer.timereport.fx.table.columns.context.IssueCellContextMenu
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatDate
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
import de.pbauerochse.worklogviewer.view.GroupReportRow
import de.pbauerochse.worklogviewer.view.IssueReportRow
import de.pbauerochse.worklogviewer.view.SummaryReportRow
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.scene.input.MouseButton
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
        cellValueFactory = Callback { SimpleObjectProperty(TimeSpentColumnData(columnDateProperty, it.value.value)) }
        cellFactory = Callback { TimeSpentColumnCell() }
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

private class TimeSpentColumnCell : TreeTableCell<ReportRow, TimeSpentColumnData>() {

    init {
        onMouseClicked = EventHandler {
            when {
                it.button == MouseButton.PRIMARY && it.clickCount == 2 -> showAddWorkItemDialog()
            }
        }
    }

    override fun updateItem(item: TimeSpentColumnData?, empty: Boolean) {
        super.updateItem(item, empty)

        text = null
        tooltip = null
        styleClass.removeAll(ALL)

        if (!empty && item != null) {
            val date = item.dateProperty.get()
            styleClass.add(TIME_SPENT_CELL)

            when {
                item.reportRow.isGrouping -> handleGroupBy(date, item.reportRow as GroupReportRow)
                item.reportRow.isIssue -> handleIssue(date, item.reportRow as IssueReportRow)
                item.reportRow.isSummary -> handleSummary(date, item.reportRow as SummaryReportRow)
            }

            when {
                isHighlighted(date) -> styleClass.add(HIGHLIGHT)
                isToday(date) -> styleClass.add(TODAY)
            }
        }
    }

    private fun handleGroupBy(date: LocalDate, row: GroupReportRow) {
        contextMenu = null
        text = row.getDurationInMinutes(date).takeIf { it > 0 }?.let { formatMinutes(it) }
        styleClass.add(GROUPING_CELL)
    }

    private fun handleIssue(date: LocalDate, row: IssueReportRow) {
        contextMenu = IssueCellContextMenu(row.issueWithWorkItems.issue, date)
        styleClass.addAll(ISSUE_ITEM_CELL, TIME_SPENT_CELL)

        val workItemsForDate = row.issueWithWorkItems.getWorkItemsForDate(date)
        val timeSpentInMinutes = workItemsForDate.sumOf { it.durationInMinutes }
        if (timeSpentInMinutes > 0) {
            val workItemsAsString = workItemsForDate.joinToString(prefix = "\n\n", separator = "\n") { workItem ->
                listOfNotNull(
                    workItem.owner.label,
                    formatMinutes(workItem.durationInMinutes),
                    workItem.workType?.label,
                    workItem.description.takeIf { it.isNotBlank() }
                ).joinToString(separator = " - ")
            }

            text = formatMinutes(timeSpentInMinutes)
            tooltip = Tooltip("${row.issueWithWorkItems.issue.fullTitle}\n\n${tableColumn.text} : $text$workItemsAsString")
        }
    }

    private fun handleSummary(date: LocalDate, row: SummaryReportRow) {
        styleClass.addAll(SUMMARY_CELL, GRAND_SUMMARY_CELL)
        contextMenu = null
        text = row.getDurationInMinutes(date).takeIf { it > 0 }?.let { formatMinutes(it) }
    }

    private fun showAddWorkItemDialog() {
        val issueContextMenu = contextMenu as IssueCellContextMenu?
        issueContextMenu?.showAddWorkItemToIssueDialog()
    }

    companion object {
        private fun isToday(date: LocalDate): Boolean = date.isEqual(LocalDate.now())
        private fun isHighlighted(date: LocalDate): Boolean = SettingsUtil.settings.highlightState.isSet(date.dayOfWeek)
    }
}
