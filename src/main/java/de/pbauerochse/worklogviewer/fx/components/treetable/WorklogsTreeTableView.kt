package de.pbauerochse.worklogviewer.fx.components.treetable

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

    internal fun setIssues(issues : List<Issue>) {
        LOGGER.debug("Showing ${issues.size} Issues")
        val treeRows = convertToTreeRows(issues)
        root.children.addAll(treeRows)
    }

    private fun convertToTreeRows(issues: List<Issue>): List<TreeItem<TreeTableRowModel>> {
        return issues
            .map { IssueTreeTableRow(it) as TreeTableRowModel }
            .map { TreeItem(it) }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorklogsTreeTableView::class.java)
    }

}