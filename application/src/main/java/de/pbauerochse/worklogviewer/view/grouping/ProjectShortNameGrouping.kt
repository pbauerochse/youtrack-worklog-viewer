package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.GroupReportRow
import de.pbauerochse.worklogviewer.view.IssueReportRow

/**
 * Groups WorklogItems by the project its issue belongs to
 */
internal object ProjectShortNameGrouping : Grouping {
    override val id: String = "PROJECT"
    override val label: String = getFormatted("grouping.project.short")

    override fun rows(issues: List<IssueWithWorkItems>): List<ReportRow> {
        return issues
            .groupBy { it.issue.project.shortName }
            .map { createGroup(it.key, it.value) }
    }

    private fun createGroup(label: String, issues: List<IssueWithWorkItems>): ReportRow {
        val issueRows = issues.asSequence()
            .map { IssueReportRow(it) }
            .toList()
        return GroupReportRow(label, issueRows)
    }

    override fun toString(): String = javaClass.name
}