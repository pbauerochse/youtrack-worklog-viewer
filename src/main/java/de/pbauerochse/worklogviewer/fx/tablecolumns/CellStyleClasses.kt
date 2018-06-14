package de.pbauerochse.worklogviewer.fx.tablecolumns

/**
 * Defines all used CSS classes to style
 * the components in the worklog viewer
 */
object CellStyleClasses {

    const val SUMMARY_CELL = "summary-cell"
    const val TIMESPENT_CELL = "timespent-cell"
    const val GROUP_CELL = "group-cell"
    const val HIGHLIGHT_CELL = "highlight-cell"
    const val TODAY_HIGHLIGHT_CELL = "today-highlight-cell"
    const val ISSUE_LINK_CELL = "issue-cell"

    val ALL_WORKLOGVIEWER_CLASSES: Set<String> = setOf(
        SUMMARY_CELL,
        GROUP_CELL,
        HIGHLIGHT_CELL,
        TODAY_HIGHLIGHT_CELL,
        ISSUE_LINK_CELL,
        TIMESPENT_CELL
    )

}
