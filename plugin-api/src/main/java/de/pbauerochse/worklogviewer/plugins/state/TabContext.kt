package de.pbauerochse.worklogviewer.plugins.state

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReportParameters

data class TabContext(
    val parameters : TimeReportParameters,
    val issues : List<Issue>
)