package de.pbauerochse.worklogviewer.timereport.view

import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.TimeReportParameters

/**
 * A flattened view of the [de.pbauerochse.worklogviewer.timereport.TimeReport], where any
 * grouping has already been applied
 */
class ReportView(
    val rows: List<ReportRow>,
    val issues: List<IssueWithWorkItems>,
    val reportParameters: TimeReportParameters,
    val appliedGrouping: AppliedGrouping?
)