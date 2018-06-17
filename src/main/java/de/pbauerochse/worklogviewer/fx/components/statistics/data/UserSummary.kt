package de.pbauerochse.worklogviewer.fx.components.statistics.data

import de.pbauerochse.worklogviewer.youtrack.domain.WorklogItem

internal data class UserSummary(
    val userDisplayName : String,
    val worklogs : List<WorklogItem>
) {
    val timeSpentInMinutes : Long by lazy {
        worklogs
            .map { it.durationInMinutes }
            .sum()
    }
}