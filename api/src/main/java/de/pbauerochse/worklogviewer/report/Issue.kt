package de.pbauerochse.worklogviewer.report

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Representation of a YouTrack issue
 * having WorklogEntries
 */
data class Issue(
    val id : String,
    val description : String,
    val estimateInMinutes : Long,
    var resolutionDate : LocalDateTime? = null
) : Comparable<Issue> {

    /**
     * Allows "cloning" an Issue. The values, except
     * the worklog items, from the other Issue are
     * applied to the new instance
     */
    constructor(issue: Issue, worklogItems: List<WorklogItem>) : this(issue.id, issue.description, issue.estimateInMinutes, issue.resolutionDate) {
        this.worklogItems.addAll(worklogItems)
    }

    // must not be contained in constructor args
    // as it has a back reference to this issue
    // and will then be contained in hashCode, equals and toString
    // which leads to an infinite loop
    val worklogItems : MutableList<WorklogItem> = mutableListOf()

    val project: String by lazy {
        PROJECT_ID_REGEX.matchEntire(id)!!.groupValues[1]
    }

    val fullTitle : String by lazy {
        "$id - $description"
    }

    /**
     * Returns the total time spent in this Issue
     * on the given date
     */
    fun getTimeSpentOn(date: LocalDate) = worklogItems
        .filter { it.date == date }
        .map { it.durationInMinutes }
        .sum()

    /**
     * Returns the total time in minutes that
     * has been logged on this issue in the
     * defined time period
     */
    fun getTotalTime(): Long = worklogItems
        .map { it.durationInMinutes }
        .sum()

    private val issueNumber: Long by lazy {
        PROJECT_ID_REGEX.matchEntire(id)!!.groupValues[2].toLong()
    }

    override fun compareTo(other: Issue): Int {
        val byProject = project.compareTo(other.project)
        return when (byProject) {
            0 -> issueNumber.compareTo(other.issueNumber)
            else -> byProject
        }
    }

    companion object {
        private val PROJECT_ID_REGEX = Regex("^(.+)-(\\d+)$")
    }
}