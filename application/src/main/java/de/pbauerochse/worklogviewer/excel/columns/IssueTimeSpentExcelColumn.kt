package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext.Companion.groupByTimeSpentStyle
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext.Companion.issueSummaryStyle
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext.Companion.issueTimeSpentStyle
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatDate
import java.time.LocalDate

/**
 * Renders the time spent for the given
 * date. Depending on the settings as
 * YouTrack timestamp (1d 4h 20m) or as
 * hours in decimal format (12,3)
 */
class IssueTimeSpentExcelColumn(private val date: LocalDate) : ExcelColumnRenderer {

    override val headline: String = formatDate(date)

    override fun write(context: ExcelRenderContext, columnIndex: Int, reportRow: ReportRow) {
        when {
            reportRow.isGrouping -> renderGroupBySummary(context, columnIndex, reportRow)
            reportRow.isIssue -> renderIssueSummary(context, columnIndex, reportRow)
            reportRow.isSummary -> renderSummary(context, columnIndex, reportRow)
        }
    }

    private fun renderGroupBySummary(context: ExcelRenderContext, columnIndex: Int, reportRow: ReportRow) {
        val totalTimeSpentInMinutes = reportRow.getDurationInMinutes(date)
        if (totalTimeSpentInMinutes > 0) {
            context
                .setTimeSpent(columnIndex, totalTimeSpentInMinutes)
                .style(columnIndex, groupByTimeSpentStyle)
        }
    }

    private fun renderIssueSummary(context: ExcelRenderContext, columnIndex: Int, reportRow: ReportRow) {
        val timeSpentInMinutes = reportRow.getDurationInMinutes(date)
        if (timeSpentInMinutes > 0) {
            context
                .setTimeSpent(columnIndex, timeSpentInMinutes)
                .style(columnIndex, issueTimeSpentStyle)
        }
    }

    private fun renderSummary(context: ExcelRenderContext, columnIndex: Int, reportRow: ReportRow) {
        val totalTimeSpentInMinutes = reportRow.getDurationInMinutes(date)
        if (totalTimeSpentInMinutes > 0) {
            context
                .setTimeSpent(columnIndex, totalTimeSpentInMinutes)
                .style(columnIndex, issueSummaryStyle)
        }
    }
}
