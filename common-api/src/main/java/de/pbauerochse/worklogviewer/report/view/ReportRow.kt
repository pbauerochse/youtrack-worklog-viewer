package de.pbauerochse.worklogviewer.report.view

import java.time.LocalDate

/**
 * Flattened representation of a [de.pbauerochse.worklogviewer.report.TimeReport]
 * which can be used to generate an overview table
 */
interface FlatReportRow {
    val label : String
    val isIssue: Boolean
    val isGrouping : Boolean
    val isSummary : Boolean

    fun getDurationInMinutes(date : LocalDate) : Long
    val totalDurationInMinutes : Long
}

/**
 * Representation of the [de.pbauerochse.worklogviewer.report.TimeReport]
 * as a table row
 */
interface ReportRow : FlatReportRow {
    val children : List<ReportRow>
}