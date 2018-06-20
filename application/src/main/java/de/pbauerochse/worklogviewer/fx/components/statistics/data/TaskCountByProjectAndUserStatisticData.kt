package de.pbauerochse.worklogviewer.fx.components.statistics.data

import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogItem

/**
 * Groups the found issues by user and
 * then by project and displays cumulative
 * data for each project
 */
class TaskCountByProjectAndUserStatisticData(issues : List<Issue>) {

    internal val projectStatistic : List<ProjectStatistic> by lazy { extractProjectStatistics(issues) }

    internal val numberOfUsers : Int by lazy {
        projectStatistic
            .flatMap { it.userStatistics }
            .map { it.userDisplayName }
            .distinct()
            .count()
    }

    private fun extractProjectStatistics(issues: List<Issue>): List<ProjectStatistic> {
        val worklogsGroupedByProject = issues
            .flatMap { it.worklogItems }
            .groupBy { it.issue.project }
            .toSortedMap()

        return worklogsGroupedByProject.map {
            val projectName = it.key
            val worklogsForThisProject = it.value

            val userStatistics = getUserSummaries(worklogsForThisProject)

            ProjectStatistic(projectName, userStatistics)
        }
    }

    private fun getUserSummaries(worklogsForProject: List<WorklogItem>): List<UserSummary> {
        return worklogsForProject
            .groupBy { it.userDisplayname }
            .toSortedMap()
            .map {
                UserSummary(it.key, it.value)
            }
    }
}