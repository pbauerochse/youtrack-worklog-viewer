package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext.Companion.groupByHeadlineStyle
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext.Companion.issueSummaryStyle
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext.Companion.regularIssueStyle
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext.Companion.resolvedIssueStyle
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.IssueReportRow

/**
 * Writes the issue description
 */
class IssueLinkExcelColumn : ExcelColumnRenderer {

    override val headline: String = getFormatted("view.main.issue")

    override fun write(context: ExcelRenderContext, columnIndex: Int, reportRow: ReportRow) {
        when {
            reportRow.isGrouping -> renderGroupByHeadline(context, columnIndex, reportRow)
            reportRow.isIssue -> renderIssue(context, columnIndex, reportRow as IssueReportRow)
            reportRow.isSummary -> renderSummary(context, columnIndex, reportRow)
        }
    }

    private fun renderGroupByHeadline(context: ExcelRenderContext, cell: Int, value: ReportRow) {
        context
            .value(cell, value.label)
            .style(cell, groupByHeadlineStyle)
    }

    private fun renderIssue(context: ExcelRenderContext, cell: Int, value: IssueReportRow) {
        val resolved = value.issueWithWorkItems.issue.isResolved
        context
            .hyperLink(cell, value.label, value.issueWithWorkItems.issue.externalUrl)
            .style(cell, if (resolved) resolvedIssueStyle else regularIssueStyle)
    }

    private fun renderSummary(context: ExcelRenderContext, cell: Int, value: ReportRow) {
        context
            .value(cell, value.label)
            .style(cell, issueSummaryStyle)
    }
}
