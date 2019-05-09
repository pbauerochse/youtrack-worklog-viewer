package de.pbauerochse.worklogviewer.fx.components.treetable.data

import de.pbauerochse.worklogviewer.fx.components.treetable.columns.GroupedIssuesTreeTableRow
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.view.ReportGroup
import de.pbauerochse.worklogviewer.view.ReportView
import de.pbauerochse.worklogviewer.view.grouping.Grouping
import javafx.scene.control.TreeItem

/**
 * Contains the data required for the [WorklogsTreeTableView]
 * to render the results
 */
object TreeItemConverter {

    fun convert(reportView: ReportView): TreeItem<ReportGroup> {
        val root: TreeItem<ReportGroup> = TreeItem()
        convertAndAdd(root, reportView.groups)
        return root
    }

    private fun convertAndAdd(parent: TreeItem<ReportGroup>, groups: List<ReportGroup>) {
        groups.asSequence()
            .map {
                val treeItem = TreeItem(it)
                convertAndAdd(treeItem, it.children)
                return@map treeItem
            }
            .forEach { parent.children.add(it) }
    }

    /**
     * Groups the issues by the group criteria and
     * introduces intermediate "folders" that contain
     * the actual issues
     */
    private fun convertGrouped(groupByCategory: Grouping, issues: List<Issue>): List<TreeItem<TimeReportRowModel>> {
        return getIssuesByGroup(issues)
            .map {
                val groupedRow = GroupedIssuesTreeTableRow(groupByCategory, it.key, it.value)
                val groupTreeItem = TreeItem<TimeReportRowModel>(groupedRow)
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
    private fun convertDefault(issues: List<Issue>): List<TreeItem<TimeReportRowModel>> {
        val rowModels = issues.map { IssueTreeTableRow(it) as TimeReportRowModel }.toMutableList()
        return rowModels.map { TreeItem(it) }
    }

}
