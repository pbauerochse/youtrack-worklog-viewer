package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.WorklogItem
import de.pbauerochse.worklogviewer.report.view.ReportRow
import de.pbauerochse.worklogviewer.view.GroupReportRow
import de.pbauerochse.worklogviewer.view.IssueReportRow
import de.pbauerochse.worklogviewer.view.grouping.Grouping.Companion.UNGROUPED

internal class WorklogItemBasedGrouping(
    override val id: String,
    override val label: String,
    private val worklogItemGroupingKeyExtractor: (WorklogItem) -> String?
) : Grouping {

    override fun rows(issues: List<Issue>): List<ReportRow> {
        val groupedWorklogs = groupedWorklogs(issues)

        return groupedWorklogs.map {
            val issueRows = issueRows(it.value)
            GroupReportRow(it.key, issueRows)
        }
    }

    private fun groupedWorklogs(issues: List<Issue>) = issues.asSequence()
        .flatMap { it.worklogItems.asSequence() }
        .groupBy { worklogItemGroupingKeyExtractor.invoke(it) ?: UNGROUPED }

    private fun issueRows(worklogItems: List<WorklogItem>) = worklogItems.asSequence()
        .groupBy { it.issue }
        .map { Issue(it.key, it.key.fields, it.value) }
        .map { IssueReportRow(it) }

    override fun toString(): String = "${javaClass.name} for $label"
}