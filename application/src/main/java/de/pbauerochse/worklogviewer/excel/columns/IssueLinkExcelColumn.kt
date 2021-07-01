package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.excel.POIRow
import de.pbauerochse.worklogviewer.excel.POIWorkbook
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.IssueReportRow
import org.apache.poi.ss.usermodel.Cell

/**
 * Writes the issue description
 */
class IssueLinkExcelColumn : ExcelColumnRenderer {

    override val headline: String = getFormatted("view.main.issue")

    override fun write(excelRow: POIRow, columnIndex: Int, reportRow: ReportRow) {
        val cell = excelRow.createCell(columnIndex)
        val workbook = excelRow.sheet.workbook

        when {
            reportRow.isGrouping -> renderGroupByHeadline(workbook, cell, reportRow)
            reportRow.isIssue -> renderIssue(workbook, cell, reportRow as IssueReportRow)
            reportRow.isSummary -> renderSummary(workbook, cell, reportRow)
        }
    }

    private fun renderGroupByHeadline(workbook: POIWorkbook, cell: Cell, value: ReportRow) {
        cell.cellStyle = workbook.groupByHeadlineStyle
        cell.setCellValue(value.label)
    }

    private fun renderIssue(workbook: POIWorkbook, cell: Cell, value: IssueReportRow) {
        val link = workbook.createHyperlink(value.issueWithWorkItems.issue.externalUrl)
        val cellStyle = if (value.issueWithWorkItems.issue.isResolved) workbook.resolvedIssueStyle else workbook.regularIssueStyle

        cell.hyperlink = link
        cell.setCellValue(value.label)
        cell.cellStyle = cellStyle
    }

    private fun renderSummary(workbook: POIWorkbook, cell: Cell, value: ReportRow) {
        cell.cellStyle = workbook.issueSummaryStyle
        cell.setCellValue(value.label)
    }
}
