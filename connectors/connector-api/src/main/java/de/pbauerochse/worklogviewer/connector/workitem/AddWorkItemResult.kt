package de.pbauerochse.worklogviewer.connector.workitem

import de.pbauerochse.worklogviewer.report.User
import java.time.LocalDate

data class AddWorkItemResult(
    val issueId: String,
    val user: User,
    val date: LocalDate,
    val durationInMinutes: Long,
    val description: String?,
    val workType: String?
)