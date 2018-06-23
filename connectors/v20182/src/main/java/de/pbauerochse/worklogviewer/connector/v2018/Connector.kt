package de.pbauerochse.worklogviewer.connector.v2018

import de.pbauerochse.worklogviewer.connector.GroupByParameter
import de.pbauerochse.worklogviewer.connector.ProgressCallback
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.report.TimeReportParameters

/**
 * YouTrackConnector for YouTrack 2018.2
 */
class Connector(private val http: Http) : YouTrackConnector {

    override fun getGroupByParameters(): List<GroupByParameter> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTimeReport(parameters: TimeReportParameters, progressCallback: ProgressCallback): TimeReport {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}