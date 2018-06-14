package de.pbauerochse.worklogviewer.fx.components.statistics

import de.pbauerochse.worklogviewer.youtrack.domain.Issue

data class ProjectStatistic(
    val projectId : String,
    private val issues : List<Issue>
) {

    val percentage : Double by lazy {
        0.0
    }

    val numberOfIssues : Int by lazy {
        issues.size
    }

    val timespentInMinutes : Long by lazy {
        1L
    }
}