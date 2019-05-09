package de.pbauerochse.worklogviewer.fx.components.treetable.data

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

/**
 * A row in the [TimeReportTreeTableView] containing
 * the worlogs for a certain [Issue]
 */
data class SummaryTreeTableRow(val issues: List<Issue>) : TimeReportRowModel {

    override val isSummaryRow: Boolean = true
    override val isIssueRow: Boolean = false
    override val isGroupByRow: Boolean = false
    override fun getLabel(): String = getFormatted("view.main.summary")

    /**
     * Returns the total time tracked in worklogs
     * on the given date
     */
    fun getTotalTimeSpentOn(date: LocalDate): Long = issues
        .flatMap { it.worklogItems }
        .filter { it.date == date }
        .map { it.durationInMinutes }
        .sum()

    /**
     * Returns the total minutes spent
     * on the contained issues
     */
    override fun getTotalTimeSpent(): Long = issues
        .map { it.getTotalTimeInMinutes() }
        .sum()
}