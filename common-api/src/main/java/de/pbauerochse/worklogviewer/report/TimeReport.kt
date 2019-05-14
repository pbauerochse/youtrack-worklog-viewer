package de.pbauerochse.worklogviewer.report

import java.time.LocalDateTime
import java.time.ZoneId

/**
 * The data extracted from YouTrack, distilled
 * into the domain model of the Worklog Viewer
 */
data class TimeReport(
    /**
     * The parameters this report
     * was created with
     */
    val reportParameters : TimeReportParameters,

    /**
     * The Issues and their Worklog Items
     * as retrieved from YouTrack
     */
    val issues : List<Issue>,

    /**
     * The time this TimeReport was generated at
     */
    val reportDate : LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
)