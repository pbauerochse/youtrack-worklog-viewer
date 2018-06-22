package de.pbauerochse.worklogviewer.youtrack.domain

import de.pbauerochse.worklogviewer.settings.SettingsUtil
import java.time.LocalDate

/**
 * A single Worklog Item as retrieved from YouTrack
 */
@Deprecated("")
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
    fun isOwn(): Boolean = username == SettingsUtil.settings.youTrackConnectionSettings.username

}
