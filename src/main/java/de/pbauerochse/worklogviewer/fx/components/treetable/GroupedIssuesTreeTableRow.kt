package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory
import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import java.time.LocalDate

/**
 * A headline, collapsable row in the [WorklogsTreeTableView] containing
 * one or more [Issue]s that belong to this group
 */
data class GroupedIssuesTreeTableRow(val groupCategory: GroupByCategory, val groupValue: String?, val issues: List<Issue>) : TreeTableRowModel {

    override val isSummaryRow: Boolean = false
    override val isIssueRow: Boolean = false
    override val isGroupByRow: Boolean = true
    override fun getLabel(): String = "${groupCategory.name}: '$groupValue'"

    fun totalTimeSpentOn(date: LocalDate): Long = issues
        .flatMap { it.worklogItems }
        .filter { it.date == date }
        .map { it.durationInMinutes }
        .sum()

    override fun getTotalTimeSpent(): Long = issues
        .flatMap { it.worklogItems }
        .map { it.durationInMinutes }
        .sum()
}