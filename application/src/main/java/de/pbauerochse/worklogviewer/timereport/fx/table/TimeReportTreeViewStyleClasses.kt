package de.pbauerochse.worklogviewer.timereport.fx.table

object TimeReportTreeViewStyleClasses {

    const val GROUPING_CELL = "grouping-item" // former group-cell
    const val ISSUE_ITEM_CELL = "issue-item" // former issue-cell
    const val ISSUE_TITLE_CELL = "issue-title"
    const val RESOLVED = "resolved" // former resolved-issue-cell
    const val SUMMARY_CELL = "summary-item" // former summary-cell
    const val GRAND_SUMMARY_CELL = "grand-summary-item"
    const val TIME_SPENT_CELL = "time-spent" // former timespent-cell

    const val HIGHLIGHT = "highlight" // former highlight-cell
    const val TODAY = "today" // former today-highlight-cell

    val ALL = listOf(
        GROUPING_CELL,
        ISSUE_ITEM_CELL,
        ISSUE_TITLE_CELL,
        RESOLVED,
        SUMMARY_CELL,
        GRAND_SUMMARY_CELL,
        TIME_SPENT_CELL,
        HIGHLIGHT,
        TODAY
    )

}