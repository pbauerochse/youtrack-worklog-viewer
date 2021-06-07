package de.pbauerochse.worklogviewer.timereport

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
     * The [Issue]s and their [WorkItems]
     * as retrieved from YouTrack
     */
    val issues : List<IssueWithWorkItems>

)
