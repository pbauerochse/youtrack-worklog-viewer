package de.pbauerochse.worklogviewer.fx.components.statistics.data

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.WorklogItem

/**
 * Groups the found issues by user and
 * then by project and displays cumulative
 * data for each project
 */
class TaskCountByUserAndProjectStatisticData(issues : List<Issue>) {

    internal val userStatistics : List<UserStatistic> by lazy { extractUserStatistics(issues) }

    internal val numberOfProjects : Int by lazy {
        userStatistics
            .flatMap { it.projectSummaries }
            .map { it.projectId }
            .distinct()
            .count()
    }

    private fun extractUserStatistics(issues: List<Issue>): List<UserStatistic> {
        val worklogsGroupedByUserDisplayname = issues
            .flatMap { it.worklogItems }
            .groupBy { it.user.displayName }
            .toSortedMap()

        return worklogsGroupedByUserDisplayname.map {
            val userDisplayname = it.key
            val worklogsForThisUser = it.value

            val projectStatistics = getProjectStatistics(worklogsForThisUser)

            UserStatistic(userDisplayname, projectStatistics)
        }
    }

    private fun getProjectStatistics(worklogsForUser: List<WorklogItem>): List<ProjectSummary> {
        val distinctProjects = worklogsForUser
            .map { it.issue.project.name ?: "---" }
            .distinct()
            .sorted()

        val totalTimeSpentInTimerange = worklogsForUser.map { it.durationInMinutes }.sum()

        val worklogsByProject = worklogsForUser.groupBy { it.issue.project.name ?: "---" }
        val issuesByProject = worklogsForUser
            .map { it.issue }
            .distinct()
            .groupBy { it.project.name ?: "---" }

        return distinctProjects.map {
            val totalTimeSpentInMinutesOnThisProject = worklogsByProject[it]!!.map { it.durationInMinutes }.sum()
            val numberOfWorkedIssuesInThisProject = issuesByProject[it]!!.count()
            val percentOfTimeSpentOnThisProject = totalTimeSpentInMinutesOnThisProject.toDouble() / totalTimeSpentInTimerange.toDouble()
            ProjectSummary(it, percentOfTimeSpentOnThisProject, numberOfWorkedIssuesInThisProject, totalTimeSpentInMinutesOnThisProject)
        }
    }
}