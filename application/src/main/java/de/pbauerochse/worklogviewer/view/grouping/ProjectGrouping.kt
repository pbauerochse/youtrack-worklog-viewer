package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.GroupReportRow
import de.pbauerochse.worklogviewer.view.IssueReportRow

/**
 * Groups WorklogItems by the project its issue belongs to
 */
internal object ProjectGrouping : Grouping {
    override val id: String = "PROJECT"
    override val label: String = getFormatted("grouping.project")

    override fun rows(issues: List<Issue>): List<ReportRow> {
        return issues
            .groupBy { it.project.name ?: "---" }
            .map { createGroup(it.key, it.value) }
    }

    private fun createGroup(label : String, issues: List<Issue>) : ReportRow {
        val issueRows = issues.asSequence()
            .map { IssueReportRow(it) }
            .toList()
        return GroupReportRow(label, issueRows)
    }

    override fun toString(): String = javaClass.name
}