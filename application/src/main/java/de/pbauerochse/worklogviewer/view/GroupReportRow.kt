package de.pbauerochse.worklogviewer.view

import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import java.time.LocalDate

/**
 * A headline, collapsable row in the [de.pbauerochse.worklogviewer.fx.components.treetable.TimeReportTreeTableView] containing
 * one or more [IssueReportRow]s that belong to this group
 */
data class GroupReportRow(override val label: String, override val children: List<ReportRow>) : ReportRow {
    override val isGrouping: Boolean = true
    override val isIssue: Boolean = false
    override val isSummary: Boolean = false

    override fun getDurationInMinutes(date: LocalDate): Long = children.asSequence()
        .map { it.getDurationInMinutes(date) }
        .sum()

    override val totalDurationInMinutes: Long = children.asSequence()
        .map { it.totalDurationInMinutes }
        .sum()
}