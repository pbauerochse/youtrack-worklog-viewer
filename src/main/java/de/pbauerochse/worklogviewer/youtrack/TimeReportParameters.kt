package de.pbauerochse.worklogviewer.youtrack

import de.pbauerochse.worklogviewer.domain.TimerangeProvider
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory

/**
 * Contains all required parameters
 * to generate the TimeReport
 */
data class TimeReportParameters(
    val timerangeProvider: TimerangeProvider,
    val groupByCategory: GroupByCategory?
)
