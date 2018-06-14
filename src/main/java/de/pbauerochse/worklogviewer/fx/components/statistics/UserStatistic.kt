package de.pbauerochse.worklogviewer.fx.components.statistics

data class UserStatistic(
    val userDisplayLabel : String,
    val projectStatistics: List<ProjectStatistic>
) {

    val totalNumberOfTickets : Long by lazy {
        projectStatistics
            .map { it.numberOfIssues }
            .sum()
    }
}