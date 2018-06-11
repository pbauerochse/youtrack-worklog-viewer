package de.pbauerochse.worklogviewer.youtrack.domain

import java.time.LocalDateTime

/**
 * Represents a YouTrack issue
 */
data class Issue(
    val issueId: String,
    val issueDescription: String,
    val estimateInMinutes: Long
) : Comparable<Issue> {
    val worklogItems: MutableList<WorklogItem> = mutableListOf()
    var resolved: LocalDateTime? = null

    val project: String by lazy {
        PROJECT_ID_REGEX.matchEntire(issueId)!!.groupValues[1]
    }

    val issueNumber: Long by lazy {
        PROJECT_ID_REGEX.matchEntire(issueId)!!.groupValues[2].toLong()
    }

    fun hasOwnWorklogs(): Boolean = worklogItems.find { it.isOwn() } != null

    override fun compareTo(other: Issue): Int {
        val byProject = project.compareTo(other.project)
        return if (byProject == 0) {
            issueNumber.compareTo(other.issueNumber)
        } else {
            byProject
        }
    }

    companion object {
        private val PROJECT_ID_REGEX = Regex("^(.+)-(\\d+)$")
    }
}