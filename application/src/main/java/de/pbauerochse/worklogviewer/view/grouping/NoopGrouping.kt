package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.IssueReportRow

/**
 * Grouping that actually does not group by anything, hence
 * returns the default view
 */
internal object NoopGrouping : Grouping {
    override val id: String = "NOOP"
    override val label: String = getFormatted("grouping.none")
    override fun rows(issues: List<Issue>): List<ReportRow> = issues.asSequence()
        .map { IssueReportRow(it) }
        .toList()

    override fun toString(): String = javaClass.name
}