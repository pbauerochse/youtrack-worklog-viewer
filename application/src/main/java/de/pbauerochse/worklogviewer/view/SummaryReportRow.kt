package de.pbauerochse.worklogviewer.view

import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

data class SummaryReportRow(val issues: List<IssueWithWorkItems>) : ReportRow {
    override val isGrouping: Boolean = false
    override val isIssue: Boolean = false
    override val isSummary: Boolean = true

    override val label: String = getFormatted("view.main.summary")
    override val children: List<ReportRow> = emptyList()

    override val totalDurationInMinutes: Long = issues
        .sumOf { it.totalTimeInMinutes }

    override fun getDurationInMinutes(date: LocalDate): Long = issues.asSequence()
        .map { it.getWorkItemsForDate(date).sumOf { workItem -> workItem.durationInMinutes } }
        .sum()
}

