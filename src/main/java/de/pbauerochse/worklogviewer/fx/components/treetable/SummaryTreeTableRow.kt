package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.youtrack.domain.Issue

/**
 * A row in the [WorklogsTreeTableView] containing
 * the worlogs for a certain [Issue]
 */
data class SummaryTreeTableRow(val issues: List<Issue>) : TreeTableRowModel {

    override val isSummaryRow: Boolean = true
    override val isIssueRow: Boolean = false
    override val isGroupByRow: Boolean = false
    override fun getLabel(): String = getFormatted("view.main.summary")
}