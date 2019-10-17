package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.view.ReportRow
import de.pbauerochse.worklogviewer.trimToNull
import de.pbauerochse.worklogviewer.view.GroupReportRow
import de.pbauerochse.worklogviewer.view.IssueReportRow
import de.pbauerochse.worklogviewer.view.grouping.Grouping.Companion.UNGROUPED

/**
 * Groups [Issue]s by the value of one of it's fields
 */
internal class FieldBasedGrouping(private val field: String) : Grouping {

    override val id: String = "FIELDBASED_$field"
    override val label: String = field
    override fun rows(issues: List<Issue>): List<ReportRow> = issues.asSequence()
        .groupBy { getFieldValue(it) }
        .map {
            val issueRows = it.value.asSequence()
                .map { issue -> IssueReportRow(issue) }
                .toList()
            GroupReportRow(it.key, issueRows)
        }

    private fun getFieldValue(issue: Issue): String {
        if (issue.fields.isEmpty()) {
            return UNGROUPED
        }

        val field = issue.fields.find { field == it.name }
        val fieldValuesAsString = field?.value?.filter { it.isNotBlank() }?.sorted()?.joinToString(", ")?.trimToNull()
        return fieldValuesAsString ?: UNGROUPED
    }

    override fun toString(): String = "${javaClass.name} for field $field"
}
