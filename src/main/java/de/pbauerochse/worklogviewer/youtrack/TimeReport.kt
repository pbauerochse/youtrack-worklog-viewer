package de.pbauerochse.worklogviewer.youtrack

/**
 * Contains all the data acquired from
 * YouTrack.
 */
data class TimeReport(
    val parameters: TimeReportParameters
) {
    fun getOwnWorklogs(): List<Any> {
        TODO("not implemented")
    }

    fun getProjectSpecificWorklogs() : List<ProjectSpecificWorklogs> {
        TODO("not implemented")
    }

}
