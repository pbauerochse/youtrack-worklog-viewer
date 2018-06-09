package de.pbauerochse.worklogviewer.youtrack.domain

import java.time.LocalDateTime

/**
 * Represents a YouTrack issue
 */
data class Issue(
    val issueId: String,
    val issueDescription: String,
    val estimateInMinutes: Long
) {

    val worklogItems: MutableList<WorklogItem> = mutableListOf()
    var resolved: LocalDateTime? = null

    val project: String by lazy {
        PROJECT_ID_REGEX.matchEntire(issueId)!!.groupValues[1]
    }

    companion object {
        private val PROJECT_ID_REGEX = Regex("^(.+)-\\d+$")
    }
}