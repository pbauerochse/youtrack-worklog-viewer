package de.pbauerochse.worklogviewer.timereport.fx.table

import de.pbauerochse.worklogviewer.fx.components.treetable.TreeItemConverter
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.timereport.fx.table.columns.IssueLinkColumn
import de.pbauerochse.worklogviewer.timereport.fx.table.columns.IssueTimeSpentColumn
import de.pbauerochse.worklogviewer.timereport.fx.table.columns.SummaryColumn
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import javafx.scene.control.TreeTableView
import org.slf4j.LoggerFactory
import java.time.temporal.ChronoUnit

/**
 * [TreeTableView] that displays a list of [de.pbauerochse.worklogviewer.timereport.Issue]s
 */
class TimeReportTreeTableView : TreeTableView<ReportRow>() {

    init {
        isShowRoot = false
        styleClass.add("time-report-table")
        addListeners()
    }

    internal fun update(reportView: ReportView) {
        LOGGER.debug("Showing ${reportView.issues.size} Issues")
        updateColumns(reportView)
        root = TreeItemConverter.convert(reportView)
    }

    private fun addListeners() {
        SettingsUtil.settingsViewModel.workhoursProperty.addListener { _, _, _ -> this.refresh() }
    }

    private fun updateColumns(data: ReportView) {
        if (columns.isEmpty()) {
            columns.add(IssueLinkColumn())
            columns.add(SummaryColumn())
        }

        val firstWorklogColumnIndex = 1 // 0 = IssueLink, 1 to n = Worklogs, n + 1 = Summary

        val startDate = data.reportParameters.timerange.start
        val daysBetweenStartAndEndDate = ChronoUnit.DAYS
            .between(startDate, data.reportParameters.timerange.end)
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

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TimeReportTreeTableView::class.java)
    }

}