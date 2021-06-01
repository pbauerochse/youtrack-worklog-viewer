package de.pbauerochse.worklogviewer.connector.workitem

import de.pbauerochse.worklogviewer.report.Project
import de.pbauerochse.worklogviewer.report.User
import java.time.LocalDate

data class AddWorkItemResult(
    val issue: AddWorkItemResultIssue,
    val user: User,
    val date: LocalDate,
    val durationInMinutes: Long,
    val text: String?,
    val workType: String?
)

data class AddWorkItemResultIssue(
    val id: String,
    val project: Project?,
    val summary: String?,
    val description: String?
)