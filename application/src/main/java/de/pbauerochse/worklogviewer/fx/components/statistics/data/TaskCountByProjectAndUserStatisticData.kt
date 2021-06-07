package de.pbauerochse.worklogviewer.fx.components.statistics.data

import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.WorkItem


/**
 * Groups the found issues by user and
 * then by project and displays cumulative
 * data for each project
 */
class TaskCountByProjectAndUserStatisticData(issues : List<IssueWithWorkItems>) {

    internal val projectStatistic : List<ProjectStatistic> by lazy { extractProjectStatistics(issues) }

    internal val numberOfUsers : Int by lazy {
        projectStatistic
            .flatMap { it.userStatistics }
            .map { it.user.label }
            .distinct()
            .count()
    }

    private fun extractProjectStatistics(issues: List<IssueWithWorkItems>): List<ProjectStatistic> {
        return issues
            .groupBy { it.issue.project }
            .map {
                val userStatistics = getUserSummaries(it.value.flatMap { issue -> issue.workItems })
                ProjectStatistic(it.key.shortName, userStatistics)
            }
            .sortedBy { it.projectName }
    }

    private fun getUserSummaries(worklogsForProject: List<WorkItem>): List<UserSummary> {
        return worklogsForProject
            .groupBy { it.owner }
            .map { UserSummary(it.key, it.value) }
            .sortedBy { it.user.label }
    }
}