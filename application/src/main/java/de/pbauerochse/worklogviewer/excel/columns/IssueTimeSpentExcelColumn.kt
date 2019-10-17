package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.excel.POIRow
import de.pbauerochse.worklogviewer.excel.POIWorkbook
import de.pbauerochse.worklogviewer.excel.setTimeSpent
import de.pbauerochse.worklogviewer.report.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatDate
import org.apache.poi.ss.usermodel.Cell
import java.time.LocalDate

/**
 * Renders the time spent for the given
 * date. Depending on the settings as
 * YouTrack timestamp (1d 4h 20m) or as
 * hours in decimal format (12,3)
 */
class IssueTimeSpentExcelColumn(private val date: LocalDate) : ExcelColumnRenderer {

    override val headline: String = formatDate(date)

    override fun write(excelRow: POIRow, columnIndex: Int, reportRow: ReportRow) {
        val cell = excelRow.createCell(columnIndex)
        val workbook = excelRow.sheet.workbook

        when {
            reportRow.isGrouping -> renderGroupBySummary(workbook, cell, reportRow)
            reportRow.isIssue -> renderIssueSummary(workbook, cell, reportRow)
            reportRow.isSummary -> renderSummary(workbook, cell, reportRow)
        }
    }

    private fun renderGroupBySummary(workbook: POIWorkbook, cell: Cell, value: ReportRow) {
        val totalTimeSpentInMinutes = value.getDurationInMinutes(date)
        if (totalTimeSpentInMinutes > 0) {
            cell.setTimeSpent(totalTimeSpentInMinutes)
            cell.cellStyle = workbook.groupByTimeSpentStyle
        }
    }

    private fun renderIssueSummary(workbook: POIWorkbook, cell: Cell, value: ReportRow) {
        val timeSpentInMinutes = value.getDurationInMinutes(date)
        if (timeSpentInMinutes > 0) {
            cell.setTimeSpent(timeSpentInMinutes)
            cell.cellStyle = workbook.issueTimeSpentStyle
        }
    }

    private fun renderSummary(workbook: POIWorkbook, cell: Cell, value: ReportRow) {
        val totalTimeSpentInMinutes = value.getDurationInMinutes(date)
        if (totalTimeSpentInMinutes > 0) {
            cell.setTimeSpent(totalTimeSpentInMinutes)
            cell.cellStyle = workbook.issueSummaryStyle
        }
    }

}
