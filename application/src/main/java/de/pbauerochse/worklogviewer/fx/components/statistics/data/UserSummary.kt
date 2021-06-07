package de.pbauerochse.worklogviewer.fx.components.statistics.data

import de.pbauerochse.worklogviewer.timereport.User
import de.pbauerochse.worklogviewer.timereport.WorkItem

internal data class UserSummary(
    val user : User,
    val works : List<WorkItem>
) {
    val timeSpentInMinutes : Long by lazy {
        works
            .map { it.durationInMinutes }
            .sum()
    }
}