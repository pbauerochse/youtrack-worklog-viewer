package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.Field
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.view.GroupReportRow
import de.pbauerochse.worklogviewer.view.IssueReportGroup
import de.pbauerochse.worklogviewer.view.ReportGroup
import de.pbauerochse.worklogviewer.view.grouping.Grouping.Companion.UNGROUPED

/**
 * Groups [Issue]s by the value of one of it's fields
 */
internal class FieldBasedGrouping(private val field: Field) : Grouping {
    override val label: String = field.name
    override fun group(issues: List<Issue>): List<ReportGroup> = issues.asSequence()
        .groupBy { getFieldValue(it) }
        .map {
            val issueRows = it.value.asSequence()
                .map { issue -> IssueReportGroup(issue) }
                .toList()
            GroupReportRow(it.key, issueRows)
        }

    private fun getFieldValue(issue: Issue): String {
        return issue.fields.find { it.name == field.name }?.value ?: UNGROUPED
    }
}
