package de.pbauerochse.worklogviewer.fx.components.treetable.data

/**
 * Describes the DataContainer for the [TimeReportTreeTableView]
 * and represents a Row in the TreeTableView component.
 *
 * A table row might be:
 * - a row containing the [de.pbauerochse.worklogviewer.report.Issue] and its worklogs
 * - a headline for a [de.pbauerochse.worklogviewer.connector.GroupByParameter]
 * - a summary row which sums up the spent time
 */
interface TimeReportRowModel {
    fun getLabel(): String
//    fun getValueFor(date : LocalDate)
//    fun getSummary()

    val isSummaryRow: Boolean
    val isIssueRow: Boolean
    val isGroupByRow: Boolean


    fun getTotalTimeSpent(): Long

}