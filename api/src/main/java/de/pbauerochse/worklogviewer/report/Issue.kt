package de.pbauerochse.worklogviewer.report

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

    private val issueNumber: Long by lazy {
        PROJECT_ID_REGEX.matchEntire(id)!!.groupValues[2].toLong()
    }

    override fun compareTo(other: Issue): Int {
        val byProject = project.compareTo(other.project)
        return when {
            byProject == 0 -> issueNumber.compareTo(other.issueNumber)
            else -> byProject
        }
    }

    companion object {
        private val PROJECT_ID_REGEX = Regex("^(.+)-(\\d+)$")
    }
}