package de.pbauerochse.worklogviewer.connector

import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import de.pbauerochse.worklogviewer.tasks.Progress

/**
 * Connector for a YouTrack instance
 */
interface YouTrackConnector {

    /**
     * Creates and fetches the [TimeReport]
     * from the YouTrack instance
     */
    fun getTimeReport(parameters : TimeReportParameters, progress: Progress) : TimeReport

}