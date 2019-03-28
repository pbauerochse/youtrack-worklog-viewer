package de.pbauerochse.worklogviewer.connector

import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import de.pbauerochse.worklogviewer.tasks.ProgressCallback

/**
 * Connector for a YouTrack instance
 */
interface YouTrackConnector {

    /**
     * Fetches the possible [GroupByParameter]s
     * by which the issues can be grouped
     */
    fun getGroupByParameters() : List<GroupByParameter>

    /**
     * Creates and fetches the [TimeReport]
     * from the YouTrack instance
     */
    fun getTimeReport(parameters : TimeReportParameters, progressCallback: ProgressCallback) : TimeReport

}