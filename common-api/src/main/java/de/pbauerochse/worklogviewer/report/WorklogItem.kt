package de.pbauerochse.worklogviewer.report

import java.time.LocalDate

/**
 * Represents a single time booking entry
 */
data class WorklogItem(
    val issue : Issue,
    val user : User,
    val date : LocalDate,
    val durationInMinutes : Long,
    val description : String?,
    val workType : String?,
    @Deprecated ("grouping will be done via 'views'") val groupingKey : String? // TODO remove this field
)