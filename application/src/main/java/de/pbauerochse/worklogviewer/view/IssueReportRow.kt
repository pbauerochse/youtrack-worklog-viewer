package de.pbauerochse.worklogviewer.view

import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import java.time.LocalDate

/**
 * A row containing the work items for a certain [Issue]
 */
data class IssueReportRow(val issueWithWorkItems: IssueWithWorkItems) : ReportRow {
    override val isGrouping: Boolean = false
    override val isIssue: Boolean = true
    override val isSummary: Boolean = false

    override val label: String = issueWithWorkItems.issue.fullTitle
    override val children: List<ReportRow> = emptyList()
    override val totalDurationInMinutes: Long = issueWithWorkItems.totalTimeInMinutes

    override fun getDurationInMinutes(date: LocalDate): Long = issueWithWorkItems.getWorkItemsForDate(date).sumOf { it.durationInMinutes }
}