package de.pbauerochse.worklogviewer.timereport

import java.net.URL
import java.time.ZonedDateTime

interface Issue: Comparable<Issue> {

    /**
     * Unique ID to distinguish this [Issue]
     * from others. This field contains the
     * technical id (e.g. Database Id from the source system)
     */
    val id: String

    /**
     * A number mainly used for comparison / ordering
     * purposes. Should also unique per [Issue]
     */
    val issueNumber: Long

    /**
     * A short label which will be used in the frontend
     * of the application. Should be short enough to not
     * break the UI but still allow the user to quickly
     * identify it at first glance.
     */
    val humanReadableId: String

    /**
     * The location where the details of this [Issue] can
     * be viewed in the browser
     */
    val externalUrl: URL

    /**
     * The title / summary of this [Issue]
     */
    val title: String

    /**
     * The description of the [Issue]
     */
    val description: String

    /**
     * The [Project] this [Issue] belongs to
     */
    val project: Project

    /**
     * if this [Issue] has been marked as resolved,
     * this field contains the timestamp when this
     * has been done, else null
     */
    val resolutionDate: ZonedDateTime?

    /**
     * additional properties as Key-Value Pairs
     */
    val fields: List<Field>

    /**
     * The `humanReadableId` and the `title` combined in one field
     */
    val fullTitle: String
        get() = "$humanReadableId - $title"

    override fun compareTo(other: Issue): Int {
        return when (val byProject = project.shortName.compareTo(other.project.shortName)) {
            0 -> issueNumber.compareTo(other.issueNumber)
            else -> byProject
        }
    }
}


/**
 * Representation of a YouTrack issue
 * having WorklogEntries
 */
//private data class Issueee(
//    override val id: String,
//    override val summary : String,
//    val project: Project,
//    val description: String,
//    val fields: List<Field>,
//    var resolutionDate: LocalDateTime? = null
//) : Comparable<Issue>, MinimalIssue {
//
//    /**
//     * Allows "cloning" an Issue. The values, except
//     * the worklog items, from the other Issue are
//     * applied to the new instance
//     */
//    constructor(issue: Issue, fields: List<Field>, worklogItems: List<WorklogItem>) : this(issue.id, issue.summary, issue.project, issue.description, fields, issue.resolutionDate) {
//        this.worklogItems.addAll(worklogItems)
//    }
//
//    // must not be contained in constructor args
//    // as it has a back reference to this issue
//    // and will then be contained in hashCode, equals and toString
//    // which leads to an infinite loop
//    val worklogItems: MutableList<WorklogItem> = mutableListOf()
//
//    val fullTitle: String by lazy {
//        "$id - $summary"
//    }
//
//    /**
//     * Returns the total time spent in this Issue
//     * on the given date
//     */
//    fun getTimeInMinutesSpentOn(date: LocalDate) = worklogItems
//        .filter { it.date == date }
//        .sumOf { it.durationInMinutes }
//
//    /**
//     * Returns the total time in minutes that
//     * has been logged on this issue in the
//     * defined time period
//     */
//    fun getTotalTimeInMinutes(): Long = worklogItems
//        .sumOf { it.durationInMinutes }
//
//    override val projectId: String
//        get() = project.id
//
//    private val issueNumber: Long by lazy {
//        id.substringAfterLast('-').toLong()
//    }
//
//    override fun compareTo(other: Issue): Int {
//        val byProject = if (project.shortName != null && other.project.shortName != null) {
//            project.shortName.compareTo(other.project.shortName)
//        } else 0
//
//        return when (byProject) {
//            0 -> issueNumber.compareTo(other.issueNumber)
//            else -> byProject
//        }
//    }
//}
