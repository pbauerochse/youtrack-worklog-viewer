package de.pbauerochse.worklogviewer.datasource

import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.timereport.*

/**
 * Connector for a YouTrack instance
 */
interface TimeTrackingDataSource {

    /**
     * Creates and fetches the [TimeReport]
     * from the YouTrack instance
     */
    fun getTimeReport(parameters: TimeReportParameters, progress: Progress): TimeReport

    /**
     * Tries to submit a new WorkItem to YouTrack
     */
    fun addWorkItem(request: AddWorkItemRequest, progress: Progress): AddWorkItemResult

    /**
     * Searches for [Issue]s by a YT query.
     */
    fun searchIssues(query: String, offset: Int, maxResults: Int, progress: Progress): List<Issue>

    /**
     * Returns the given [Issue] with all of its [WorkItem]s
     */
    fun loadWorkItems(issue: Issue, timeRange: TimeRange?, progress: Progress): IssueWithWorkItems

    /**
     * Loads the details for an [Issue] by id
     */
    fun loadIssue(id: String, progress: Progress): Issue

    /**
     * returns the [WorkItemType]s currently valid for the
     * given project
     */
    fun getWorkItemTypes(projectId: String, progress: Progress): List<WorkItemType>

    fun loadIssuesByIds(issueIds: Set<String>, progress: Progress): List<Issue>

}