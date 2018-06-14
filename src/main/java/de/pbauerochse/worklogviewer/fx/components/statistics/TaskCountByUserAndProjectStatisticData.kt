package de.pbauerochse.worklogviewer.fx.components.statistics

import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogItem

/**
 * Groups the found issues by user and
 * then by project and displays cumulative
 * data for each project
 */
class TaskCountByUserAndProjectStatisticData(issues : List<Issue>) {

    val userStatistics : List<UserStatistic> = extractStatistics(issues)

    private fun extractStatistics(issues: List<Issue>): List<UserStatistic> {
        val worklogsGroupedByUserDisplayname = issues
            .flatMap { it.worklogItems }
            .groupBy { it.userDisplayname }

        return worklogsGroupedByUserDisplayname.map {
            val userDisplayname = it.key
            val worklogsForThisUser = it.value

            val projectStatistics = getProjectStatistics(worklogsForThisUser)

            UserStatistic(userDisplayname, projectStatistics)
        }
    }

    private fun getProjectStatistics(worklogsForUser: List<WorklogItem>): List<ProjectStatistic> {
        val worklogsByProject = worklogsForUser.groupBy { it.issue.project }
        // TODO

    }


}