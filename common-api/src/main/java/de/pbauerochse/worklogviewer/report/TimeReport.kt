package de.pbauerochse.worklogviewer.report

/**
 * The data extracted from YouTrack, distilled
 * into the domain model of the Worklog Viewer
 */
class TimeReport(

    /**
     * The parameters this report
     * was created with
     */
    val reportParameters : TimeReportParameters,

    /**
     * The Issues and their Worklog Items
     * as retrieved from YouTrack
     */
    val issues : List<Issue>

)
