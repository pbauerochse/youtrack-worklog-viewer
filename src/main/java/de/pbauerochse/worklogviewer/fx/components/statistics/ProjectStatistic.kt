package de.pbauerochse.worklogviewer.fx.components.statistics

data class ProjectStatistic(
    val projectId: String,
    val percentage: Double,
    val numberOfIssues: Int,
    val timeSpentInMinutes: Long
)