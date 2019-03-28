package de.pbauerochse.worklogviewer.plugin

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReportParameters

data class TabContext(
    val parameters : TimeReportParameters,
    val issues : List<Issue>
)