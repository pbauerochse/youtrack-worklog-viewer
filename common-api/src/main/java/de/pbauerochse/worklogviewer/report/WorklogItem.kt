package de.pbauerochse.worklogviewer.report

import java.time.LocalDate

/**
 * Represents a single
 */
data class WorklogItem(
    val issue : Issue,
    val user : User,
    val date : LocalDate,
    val durationInMinutes : Long,
    val description : String?,
    val workType : String?,
    val groupingKey : String?
)