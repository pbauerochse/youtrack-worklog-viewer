package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext.Companion.groupByTimeSpentStyle
import de.pbauerochse.worklogviewer.excel.ExcelRenderContext.Companion.issueSummaryStyle
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

/**
 * Renders the last summary column
 */
class SummaryExcelColumn : ExcelColumnRenderer {

    override val headline: String = getFormatted("view.main.summary")

    override fun write(context: ExcelRenderContext, columnIndex: Int, reportRow: ReportRow) {
        val timeSpentInMinutes = reportRow.totalDurationInMinutes
        context
            .setTimeSpent(columnIndex, timeSpentInMinutes)
            .style(columnIndex, if (reportRow.isGrouping) groupByTimeSpentStyle else issueSummaryStyle)
    }

}
