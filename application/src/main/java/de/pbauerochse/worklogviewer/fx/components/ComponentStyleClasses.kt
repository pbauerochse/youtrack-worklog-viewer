package de.pbauerochse.worklogviewer.fx.components

/**
 * Defines all used CSS classes to style
 * the components in the worklog viewer
 */
object ComponentStyleClasses {

    const val SUMMARY_CELL = "summary-cell"
    const val TIMESPENT_CELL = "timespent-cell"
    const val GROUP_TITLE_CELL = "group-title-cell"
    const val GROUP_CELL = "group-cell"
    const val HIGHLIGHT_CELL = "highlight-cell"
    const val TODAY_HIGHLIGHT_CELL = "today-highlight-cell"
    const val ISSUE_LINK_CELL = "issue-cell"
    const val RESOLVED_ISSUE_CELL = "resolved-issue-cell"
    const val STATISTICS_PANEL = "statistics"
    const val TREE_GROUP_PARENT = "tree-parent"

    val ALL_WORKLOGVIEWER_CLASSES: Set<String> = setOf(
        SUMMARY_CELL,
        TIMESPENT_CELL,
        GROUP_TITLE_CELL,
        GROUP_CELL,
        HIGHLIGHT_CELL,
        TODAY_HIGHLIGHT_CELL,
        ISSUE_LINK_CELL,
        RESOLVED_ISSUE_CELL,
        STATISTICS_PANEL,
        TREE_GROUP_PARENT
    )

}
