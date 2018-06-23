package de.pbauerochse.worklogviewer.fx.components.treetable

/**
 * Describes the DataContainer for the [WorklogsTreeTableView]
 * and represents a Row in the TreeTableView component.
 *
 * A table row might be:
 * - a row containing the [de.pbauerochse.worklogviewer.report.Issue] and its worklogs
 * - a headline for a [de.pbauerochse.worklogviewer.connector.GroupByParameter]
 * - a summary row which sums up the spent time
 */
interface TreeTableRowModel {

    val isSummaryRow: Boolean
    val isIssueRow: Boolean
    val isGroupByRow: Boolean

    fun getLabel(): String
    fun getTotalTimeSpent(): Long

}