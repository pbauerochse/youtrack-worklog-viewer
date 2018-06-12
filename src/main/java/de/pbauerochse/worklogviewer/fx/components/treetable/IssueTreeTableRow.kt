package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.youtrack.domain.Issue

/**
 * A row in the [WorklogsTreeTableView] containing
 * the worlogs for a certain [Issue]
 */
data class IssueTreeTableRow(override val issue: Issue) : TreeTableRowModel {

    override val isSummaryRow: Boolean = false
    override val isIssueRow: Boolean = true
    override val isGroupByRow: Boolean = false

}