package de.pbauerochse.worklogviewer.view

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

data class SummaryReportRow(val issues: List<Issue>) : ReportRow {
    override val isGrouping: Boolean = false
    override val isIssue: Boolean = false
    override val isSummary: Boolean = true

    override val label: String = getFormatted("view.main.summary")
    override val children: List<ReportRow> = emptyList()

    override val totalDurationInMinutes: Long = issues.asSequence()
        .map { it.getTotalTimeInMinutes() }
        .sum()

    override fun getDurationInMinutes(date: LocalDate): Long = issues.asSequence()
        .map { it.getTimeInMinutesSpentOn(date) }
        .sum()
}

