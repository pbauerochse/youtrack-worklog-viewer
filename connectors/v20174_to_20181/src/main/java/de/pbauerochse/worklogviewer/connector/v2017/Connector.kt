package de.pbauerochse.worklogviewer.connector.v2017

import de.pbauerochse.worklogviewer.connector.GroupByParameter
import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.report.TimeReportParameters

/**
 * Connector for YouTrack 2017.4 to 2018.1
 */
class Connector(private val settings : YouTrackConnectionSettings) : YouTrackConnector {

    override fun getGroupByParameters(): List<GroupByParameter> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTimeReport(parameters: TimeReportParameters): TimeReport {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}