package de.pbauerochse.worklogviewer.timereport

import java.time.LocalDate

/**
 * Groups an [Issue] with its [WorkItem]s
 */
class IssueWithWorkItems(
    val issue: Issue,
    val workItems: List<WorkItem>
): Comparable<IssueWithWorkItems> {

    /**
     * Returns `true` if any of the [WorkItem]s
     * were created by the current user.
     */
    val hasWorkItemsBelongingToCurrentUser: Boolean
        get() = workItems.any { it.belongsToCurrentUser }

    /**
     * Returns all [WorkItem]s that are for the given LocalDate
     */
    fun getWorkItemsForDate(date: LocalDate): List<WorkItem> {
        return workItems.filter { it.workDateAtLocalZone.toLocalDate() == date }
    }

    /**
     * Returns the total time in minutes spent on this [Issue],
     * by summing up the `durationInMinutes` of each [WorkItem]
     */
    val totalTimeInMinutes: Long
        get() = workItems.sumOf { it.durationInMinutes }

    override fun compareTo(other: IssueWithWorkItems): Int = issue.compareTo(other.issue)

}