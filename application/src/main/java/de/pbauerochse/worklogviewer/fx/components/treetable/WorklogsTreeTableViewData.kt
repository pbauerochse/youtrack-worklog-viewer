package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.connector.GroupByParameter
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import javafx.scene.control.TreeItem

/**
 * Contains the data required for the [WorklogsTreeTableView]
 * to render the results
 */
class WorklogsTreeTableViewData(
    val reportParameters: TimeReportParameters,
    val issues: List<Issue>
) {

    internal val treeRows: List<TreeItem<TreeTableRowModel>> by lazy {
        val data = if (reportParameters.groupByParameter != null) {
            convertGrouped(reportParameters.groupByParameter!!, issues)
        } else {
            convertDefault(issues).toMutableList()
        }.toMutableList()
        data.add(TreeItem(SummaryTreeTableRow(issues)))

        return@lazy data
    }

    /**
     * Groups the issues by the group criteria and
     * introduces intermediate "folders" that contain
     * the actual issues
     */
    private fun convertGrouped(groupByCategory: GroupByParameter, issues: List<Issue>): List<TreeItem<TreeTableRowModel>> {
        return getIssuesByGroup(issues)
            .map {
                val groupedRow = GroupedIssuesTreeTableRow(groupByCategory, it.key, it.value)
                val groupTreeItem = TreeItem<TreeTableRowModel>(groupedRow)
                groupTreeItem.isExpanded = true

                val childItems = convertDefault(it.value)
                groupTreeItem.children.addAll(childItems)

                return@map groupTreeItem
            }
    }

    /**
     * Returns a map having the distinct group criteria
     * values as key and Issues with the worklogs belonging
     * to this group.
     *
     * Might contain the same issue in more than one group
     * (e.g. when WorklogItems are grouped by work-type)
     */
    private fun getIssuesByGroup(issues: List<Issue>): Map<String?, List<Issue>> {
        val groupToIssues = mutableMapOf<String?, List<Issue>>()

        getGroupedWorklogItems(issues)
            .forEach {
                val groupCriteria = it.key
                val groupedByGroupAndIssue = it.value
                    .groupBy { it.issue }
                    .map { Issue(it.key, it.key.fields, it.value) }

                groupToIssues[groupCriteria] = groupedByGroupAndIssue
            }

        return groupToIssues
    }

    /**
     * The GroupBy value is stored in the WorklogItem instead of the Issue,
     * so we first have to extract all WorklogItems and the group them
     * together by the group property
     */
    private fun getGroupedWorklogItems(issues: List<Issue>) = issues
        .flatMap { it.worklogItems }
        .sortedBy { it.groupingKey }
        .groupBy { it.groupingKey }

    /**
     * Converts each Issue in the list to a
     * single row in the TreeView
     */
    private fun convertDefault(issues: List<Issue>): List<TreeItem<TreeTableRowModel>> {
        val rowModels = issues.map { IssueTreeTableRow(it) as TreeTableRowModel }.toMutableList()
        return rowModels.map { TreeItem(it) }
    }

}
