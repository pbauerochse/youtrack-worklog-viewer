package de.pbauerochse.worklogviewer.report.view

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReportParameters

/**
 * A flattened view of the [de.pbauerochse.worklogviewer.report.TimeReport], where any
 * grouping has already been applied
 */
class ReportView(
    val rows: List<ReportRow>,
    val issues: List<Issue>,
    val reportParameters: TimeReportParameters
)