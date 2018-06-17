package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableView
import org.slf4j.LoggerFactory
import java.time.temporal.ChronoUnit

/**
 * Displays the [Issue]s in a TreeTableView
 */
class WorklogsTreeTableView : TreeTableView<TreeTableRowModel>() {

    init {
        isShowRoot = false
        root = TreeItem()
    }

    internal fun update(data: WorklogsTreeTableViewData) {
        LOGGER.debug("Showing ${data.issues.size} Issues")
        root.children.clear()
        selectionModel.clearSelection()

        root.children.addAll(data.treeRows)
        root.children.add(getSummaryRow(data.issues))

        updateColumns(data)
    }

    private fun updateColumns(data: WorklogsTreeTableViewData) {
        if (columns.isEmpty()) {
            // add standard columns
            // columns.add(IssueStatusColumn())
            columns.add(IssueLinkColumn())
            columns.add(SummaryColumn())
        }

        val firstWorklogColumnIndex = 1 // 0 = IssueLink, 1 to n = Worklogs, n + 1 = Summary

        val startDate = data.reportParameters.timerangeProvider.startDate
        val daysBetweenStartAndEndDate = ChronoUnit.DAYS
            .between(startDate, data.reportParameters.timerangeProvider.endDate)
            .toInt()

        for (days in 0..daysBetweenStartAndEndDate) {
            val columnDate = startDate.plusDays(days.toLong())
            val columnIndex = firstWorklogColumnIndex + days
            getOrCreateWorklogColumnAtIndex(columnIndex).update(columnDate)
        }
        removeExcessColumns(firstWorklogColumnIndex + daysBetweenStartAndEndDate + 1, columns.size - 1)
    }

    private fun getOrCreateWorklogColumnAtIndex(colIndex: Int): IssueTimeSpentColumn {
        if (columns.size <= colIndex || columns[colIndex] !is IssueTimeSpentColumn) {
            LOGGER.debug("Creating column at index $colIndex")
            columns.add(colIndex, IssueTimeSpentColumn())
        }
        return columns[colIndex] as IssueTimeSpentColumn
    }

    private fun removeExcessColumns(startIndexInclusive: Int, endIndexExclusive: Int) {
        LOGGER.debug("Removing excess columns from $startIndexInclusive to $endIndexExclusive")
        columns.remove(startIndexInclusive, endIndexExclusive)
    }

    private fun getSummaryRow(issues: List<Issue>): TreeItem<TreeTableRowModel> {
        return TreeItem(SummaryTreeTableRow(issues))
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorklogsTreeTableView::class.java)
    }

}