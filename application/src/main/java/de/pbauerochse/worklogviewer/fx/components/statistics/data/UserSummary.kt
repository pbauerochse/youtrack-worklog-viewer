package de.pbauerochse.worklogviewer.fx.components.statistics.data

import de.pbauerochse.worklogviewer.report.User
import de.pbauerochse.worklogviewer.report.WorklogItem

internal data class UserSummary(
    val user : User,
    val worklogs : List<WorklogItem>
) {
    val timeSpentInMinutes : Long by lazy {
        worklogs
            .map { it.durationInMinutes }
            .sum()
    }
}