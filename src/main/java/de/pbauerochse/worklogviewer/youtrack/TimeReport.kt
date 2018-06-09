package de.pbauerochse.worklogviewer.youtrack

import de.pbauerochse.worklogviewer.youtrack.csv.CsvReportData

/**
 * Contains all the data acquired from
 * YouTrack.
 */
data class TimeReport(
    val parameters: TimeReportParameters,
    val data: CsvReportData
) {


}
