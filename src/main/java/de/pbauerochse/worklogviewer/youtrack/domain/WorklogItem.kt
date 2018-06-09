package de.pbauerochse.worklogviewer.youtrack.domain

import java.time.LocalDate

/**
 * A single Worklog Item as retrieved from YouTrack
 */
data class WorklogItem(
    val issue : Issue,
    val username: String,
    val userDisplayname: String,
    val description: String,
    val date: LocalDate,
    val durationInMinutes: Long,
    val workType: String,
    val group: String?
) {

//    fun createDeepCopy(): WorklogItem {
//        val copy = WorklogItem()
//        copy.date = date
//        copy.durationInMinutes = durationInMinutes
//        copy.group = group
//        copy.username = username
//        copy.userDisplayname = userDisplayname
//        copy.workType = workType
//        copy.workDescription = workDescription
//
//        return copy
//    }


}
