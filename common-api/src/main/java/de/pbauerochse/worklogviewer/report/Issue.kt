package de.pbauerochse.worklogviewer.report

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Representation of a YouTrack issue
 * having WorklogEntries
 */
data class Issue(
    override val id: String,
    override val summary : String,
    val project: Project,
    val description: String,
    val fields: List<Field>,
    var resolutionDate: LocalDateTime? = null
) : Comparable<Issue>, MinimalIssue {

    /**
     * Allows "cloning" an Issue. The values, except
     * the worklog items, from the other Issue are
     * applied to the new instance
     */
    constructor(issue: Issue, fields: List<Field>, worklogItems: List<WorklogItem>) : this(issue.id, issue.summary, issue.project, issue.description, fields, issue.resolutionDate) {
        this.worklogItems.addAll(worklogItems)
    }

    // must not be contained in constructor args
    // as it has a back reference to this issue
    // and will then be contained in hashCode, equals and toString
    // which leads to an infinite loop
    val worklogItems: MutableList<WorklogItem> = mutableListOf()

    val fullTitle: String by lazy {
        "$id - $summary"
    }

    /**
     * Returns the total time spent in this Issue
     * on the given date
     */
    fun getTimeInMinutesSpentOn(date: LocalDate) = worklogItems
        .filter { it.date == date }
        .sumOf { it.durationInMinutes }

    /**
     * Returns the total time in minutes that
     * has been logged on this issue in the
     * defined time period
     */
    fun getTotalTimeInMinutes(): Long = worklogItems
        .sumOf { it.durationInMinutes }

    override val projectId: String
        get() = project.id

    private val issueNumber: Long by lazy {
        id.substringAfterLast('-').toLong()
    }

    override fun compareTo(other: Issue): Int {
        val byProject = if (project.name != null && other.project.name != null) {
            project.name.compareTo(other.project.name)
        } else 0

        return when (byProject) {
            0 -> issueNumber.compareTo(other.issueNumber)
            else -> byProject
        }
    }
}
