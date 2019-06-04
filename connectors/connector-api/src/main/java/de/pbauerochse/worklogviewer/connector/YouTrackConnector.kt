package de.pbauerochse.worklogviewer.connector

import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemRequest
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemResult
import de.pbauerochse.worklogviewer.report.Issue
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

    /**
     * Tries to submit a new WorkItem to YouTrack
     */
    fun addWorkItem(request : AddWorkItemRequest) : AddWorkItemResult

    fun searchIssues(query : String, offset : Int, progress: Progress) : List<Issue>

}
