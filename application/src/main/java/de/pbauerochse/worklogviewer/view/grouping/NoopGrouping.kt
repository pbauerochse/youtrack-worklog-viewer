package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.IssueReportGroup
import de.pbauerochse.worklogviewer.view.ReportGroup

/**
 * Grouping that actually does not group by anything, hence
 * returns the default view
 */
internal object NoopGrouping : Grouping {
    override val label: String = getFormatted("grouping.none")
    override fun group(issues: List<Issue>): List<ReportGroup> = issues.asSequence()
        .map { IssueReportGroup(it) }
        .toList()
}