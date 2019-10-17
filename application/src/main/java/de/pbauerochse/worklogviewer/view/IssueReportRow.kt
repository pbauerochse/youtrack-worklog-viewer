package de.pbauerochse.worklogviewer.view

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.view.ReportRow
import java.time.LocalDate

/**
 * A row containing the work items for a certain [Issue]
 */
data class IssueReportRow(val issue: Issue) : ReportRow {
    override val isGrouping: Boolean = false
    override val isIssue: Boolean = true
    override val isSummary: Boolean = false

    override val label: String = issue.fullTitle
    override val children: List<ReportRow> = emptyList()
    override val totalDurationInMinutes: Long = issue.getTotalTimeInMinutes()

    override fun getDurationInMinutes(date: LocalDate): Long = issue.getTimeInMinutesSpentOn(date)
}