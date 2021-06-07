package de.pbauerochse.worklogviewer.fx.components.statistics.data

import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.WorkItem

/**
 * Groups the found issues by user and
 * then by project and displays cumulative
 * data for each project
 */
class TaskCountByUserAndProjectStatisticData(issues: List<IssueWithWorkItems>) {

    internal val userStatistics: List<UserStatistic> by lazy { extractUserStatistics(issues) }

    internal val numberOfProjects: Int by lazy {
        userStatistics
            .flatMap { it.projectSummaries }
            .map { it.projectId }
            .distinct()
            .count()
    }

    private fun extractUserStatistics(issues: List<IssueWithWorkItems>): List<UserStatistic> {
        return issues
            .flatMap { issue -> issue.workItems.map { IssueWithUserWorklog(issue.issue, it) } }
            .groupBy { it.workItem.owner }
            .map {
                val projectStatistics = getProjectStatistics(it.value)
                UserStatistic(it.key.label, projectStatistics)
            }
            .sortedBy { it.userDisplayLabel }
    }

    private fun getProjectStatistics(worklogsForUser: List<IssueWithUserWorklog>): List<ProjectSummary> {
        val distinctProjects = worklogsForUser
            .map { it.issue.project.shortName }
            .distinct()
            .sorted()

        val totalTimeSpentInTimerange = worklogsForUser.sumOf { it.workItem.durationInMinutes }

        val worklogsByProject = worklogsForUser.groupBy { it.issue.project.shortName }
        val issuesByProject = worklogsForUser
            .map { it.issue }
            .distinct()
            .groupBy { it.project.shortName ?: "---" }

        return distinctProjects.map {
            val totalTimeSpentInMinutesOnThisProject = worklogsByProject[it]!!.sumOf { worklogItem -> worklogItem.workItem.durationInMinutes }
            val numberOfWorkedIssuesInThisProject = issuesByProject[it]!!.count()
            val percentOfTimeSpentOnThisProject = totalTimeSpentInMinutesOnThisProject.toDouble() / totalTimeSpentInTimerange.toDouble()
            ProjectSummary(it, percentOfTimeSpentOnThisProject, numberOfWorkedIssuesInThisProject, totalTimeSpentInMinutesOnThisProject)
        }
    }

    private data class IssueWithUserWorklog(
        val issue: Issue,
        val workItem: WorkItem
    )
}