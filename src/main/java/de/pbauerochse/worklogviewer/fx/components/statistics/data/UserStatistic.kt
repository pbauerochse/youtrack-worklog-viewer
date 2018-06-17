package de.pbauerochse.worklogviewer.fx.components.statistics.data

internal data class UserStatistic(
    val userDisplayLabel : String,
    val projectSummaries: List<ProjectSummary>
) {

    val totalNumberOfTickets : Int by lazy {
        projectSummaries
            .map { it.numberOfIssues }
            .sum()
    }

    val totalTimeSpent : Long by lazy {
        projectSummaries
            .map { it.timeSpentInMinutes }
            .sum()
    }
}