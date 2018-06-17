package de.pbauerochse.worklogviewer.fx.components.statistics.data

internal data class UserStatistic(
    val userDisplayLabel : String,
    val projectStatistics: List<ProjectStatistic>
) {

    val totalNumberOfTickets : Int by lazy {
        projectStatistics
            .map { it.numberOfIssues }
            .sum()
    }

    val totalTimeSpent : Long by lazy {
        projectStatistics
            .map { it.timeSpentInMinutes }
            .sum()
    }
}