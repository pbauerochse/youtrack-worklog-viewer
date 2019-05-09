package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.GroupReportRow
import de.pbauerochse.worklogviewer.view.IssueReportGroup
import de.pbauerochse.worklogviewer.view.ReportGroup

/**
 * Groups WorklogItems by the project its issue belongs to
 */
internal object ProjectGrouping : Grouping {
    override val label: String = getFormatted("grouping.project")

    override fun group(issues: List<Issue>): List<ReportGroup> {
        return issues.asSequence()
            .groupBy { it.project }
            .map { createGroup(it.key, it.value) }
    }

    private fun createGroup(label : String, issues: List<Issue>) : ReportGroup {
        val issueRows = issues.asSequence()
            .map { IssueReportGroup(it) }
            .toList()
        return GroupReportRow(label, issueRows)
    }
}