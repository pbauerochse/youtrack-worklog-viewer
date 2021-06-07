package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.WorkItem
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.view.GroupReportRow
import de.pbauerochse.worklogviewer.view.IssueReportRow
import de.pbauerochse.worklogviewer.view.grouping.Grouping.Companion.UNGROUPED

internal class WorklogItemBasedGrouping(
    override val id: String,
    override val label: String,
    private val worklogItemGroupingKeyExtractor: (WorkItem) -> String?
) : Grouping {

    override fun rows(issues: List<IssueWithWorkItems>): List<ReportRow> {
        return singleWorkItemWithIssue(issues)
            .groupBy { worklogItemGroupingKeyExtractor.invoke(it.workItem) ?: UNGROUPED }
            .map {
                GroupReportRow(it.key, issueRows(it.value))
            }
    }

    private fun singleWorkItemWithIssue(issues: List<IssueWithWorkItems>): List<WorkItemWithIssue> = issues
        .flatMap { issueWithWorkItem -> issueWithWorkItem.workItems.map { WorkItemWithIssue(it, issueWithWorkItem.issue) } }

    private fun issueRows(workItems: List<WorkItemWithIssue>): List<IssueReportRow> {
        return workItems
            .groupBy { it.issue }
            .map {
                val workItemsForIssue = it.value.map { workItemWithIssue -> workItemWithIssue.workItem }
                IssueWithWorkItems(it.key, workItemsForIssue)
            }
            .map { IssueReportRow(it) }
    }

    override fun toString(): String = "${javaClass.name} for $label"

    private data class WorkItemWithIssue(
        val workItem: WorkItem,
        val issue: Issue
    )
}