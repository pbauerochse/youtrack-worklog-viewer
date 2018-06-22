package de.pbauerochse.worklogviewer.youtrack.domain

import de.pbauerochse.worklogviewer.settings.SettingsUtil
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Represents a YouTrack issue
 */
@Deprecated("")
data class Issue(
    val issueId: String,
    val issueDescription: String,
    val estimateInMinutes: Long,
    var resolved: LocalDateTime? = null
) : Comparable<Issue> {

    // must not be contained in constructor ars
    // as it has a back reference to this issue
    // and will then be contained in hashCode, equals and toString
    // which leads to an infinite loop
    val worklogItems: MutableList<WorklogItem> = mutableListOf()

    /**
     * Allows "cloning" an Issue. The values
     * from the other Issue are applied to the
     * new instance
     */
    constructor(issue: Issue, worklogItems: List<WorklogItem>) : this(issue.issueId, issue.issueDescription, issue.estimateInMinutes, issue.resolved) {
        this.worklogItems.addAll(worklogItems)
    }

    val project: String by lazy {
        PROJECT_ID_REGEX.matchEntire(issueId)!!.groupValues[1]
    }

    private val issueNumber: Long by lazy {
        PROJECT_ID_REGEX.matchEntire(issueId)!!.groupValues[2].toLong()
    }

    val fullTitle : String by lazy {
        "$issueId - $issueDescription"
    }

    fun getYoutrackLink(): String {
        val baseUrl = SettingsUtil.settings.youTrackConnectionSettings.url.removeSuffix("/")
        return "$baseUrl/issue/$issueId#tab=Time%%20Tracking"
    }

    fun hasOwnWorklogs(): Boolean = worklogItems.any { it.isOwn() }

    override fun compareTo(other: Issue): Int {
        val byProject = project.compareTo(other.project)
        return when {
            byProject == 0 -> issueNumber.compareTo(other.issueNumber)
            else -> byProject
        }
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

    companion object {
        private val PROJECT_ID_REGEX = Regex("^(.+)-(\\d+)$")
    }
}