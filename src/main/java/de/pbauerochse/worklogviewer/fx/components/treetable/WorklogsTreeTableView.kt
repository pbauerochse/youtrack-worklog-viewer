package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.youtrack.TimeReportParameters
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory
import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableView
import org.slf4j.LoggerFactory

/**
 * Displays the [Issue]s in a TreeTableView
 */
class WorklogsTreeTableView : TreeTableView<TreeTableRowModel>() {

    init {
        isShowRoot = false
        root = TreeItem()
        columns.addAll(
//            IssueStatusColumn(),
            IssueLinkColumn()//,
//            IssueTimeSpentColumn(),
//            SummaryColumn()
        )
    }

    internal fun setIssues(issues: List<Issue>, reportParameters: TimeReportParameters) {
        LOGGER.debug("Showing ${issues.size} Issues")
        root.children.clear()
        selectionModel.clearSelection()

        val treeRows = convertToTreeRows(issues, reportParameters)
        root.children.addAll(treeRows)
        root.children.add(getSummaryRow(issues))
        // TODO add columns here instead of in init since we only know here which timerange is included
    }

    private fun convertToTreeRows(issues: List<Issue>, reportParameters: TimeReportParameters): List<TreeItem<TreeTableRowModel>> {
        return if (reportParameters.hasGroupByCategory) {
            convertGrouped(reportParameters.groupByCategory!!, issues)
        } else {
            convertDefault(issues)
        }
    }

    private fun getSummaryRow(issues: List<Issue>): TreeItem<TreeTableRowModel> {
        return TreeItem(SummaryTreeTableRow(issues))
    }

    /**
     * Converts each Issue in the list to a
     * single row in the TreeView
     */
    private fun convertDefault(issues: List<Issue>): List<TreeItem<TreeTableRowModel>> = issues
        .map { IssueTreeTableRow(it) as TreeTableRowModel }
        .map { TreeItem(it) }

    /**
     * Groups the issues by the group criteria and
     * introduces intermediate "folders" that contain
     * the actual issues
     */
    private fun convertGrouped(groupByCategory: GroupByCategory, issues: List<Issue>): List<TreeItem<TreeTableRowModel>> {
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
                    .map {
                        val originalIssue = it.key
                        val groupedIssue = Issue(originalIssue.issueId, originalIssue.issueDescription, originalIssue.estimateInMinutes)
                        groupedIssue.worklogItems.addAll(it.value)
                        return@map groupedIssue
                    }

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
        .sortedBy { it.group }
        .groupBy { it.group }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorklogsTreeTableView::class.java)
    }

}

private data class GroupWithIssues(val group: String?, val issues: List<Issue>)