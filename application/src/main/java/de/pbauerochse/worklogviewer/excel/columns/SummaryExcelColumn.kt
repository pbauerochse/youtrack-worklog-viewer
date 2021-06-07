package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.excel.POIRow
import de.pbauerochse.worklogviewer.excel.setTimeSpent
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

/**
 * Renders the last summary column
 */
class SummaryExcelColumn : ExcelColumnRenderer {

    override val headline: String = getFormatted("view.main.summary")

    override fun write(excelRow: POIRow, columnIndex: Int, reportRow: ReportRow) {
        val cell = excelRow.createCell(columnIndex)
        val workbook = excelRow.sheet.workbook

        val timeSpentInMinutes = reportRow.totalDurationInMinutes
        cell.setTimeSpent(timeSpentInMinutes)
        cell.cellStyle = if (reportRow.isGrouping) workbook.groupByTimeSpentStyle else workbook.issueSummaryStyle
    }

}
