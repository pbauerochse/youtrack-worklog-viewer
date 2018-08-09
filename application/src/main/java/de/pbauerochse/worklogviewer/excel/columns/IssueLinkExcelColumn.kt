package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.excel.POIRow
import de.pbauerochse.worklogviewer.excel.POIWorkbook
import de.pbauerochse.worklogviewer.fx.components.treetable.GroupedIssuesTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.IssueTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.SummaryTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.TreeTableRowModel
import de.pbauerochse.worklogviewer.getYouTrackLink
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import org.apache.poi.ss.usermodel.Cell

/**
 * Writes the issue description
 */
class IssueLinkExcelColumn : ExcelColumnRenderer {

    override val headline: String = getFormatted("view.main.issue")

    override fun write(row: POIRow, columnIndex: Int, value: TreeTableRowModel) {
        val cell = row.createCell(columnIndex)
        val workbook = row.sheet.workbook

        when {
            value.isGroupByRow -> renderGroupByHeadline(workbook, cell, value as GroupedIssuesTreeTableRow)
            value.isIssueRow -> renderIssue(workbook, cell, value as IssueTreeTableRow)
            value.isSummaryRow -> renderSummary(workbook, cell, value as SummaryTreeTableRow)
        }
    }

    private fun renderGroupByHeadline(workbook: POIWorkbook, cell: Cell, value: GroupedIssuesTreeTableRow) {
        cell.cellStyle = workbook.groupByHeadlineStyle
        cell.setCellValue(value.getLabel())
    }

    private fun renderIssue(workbook: POIWorkbook, cell: Cell, value: IssueTreeTableRow) {
        val link = workbook.createHyperlink(value.issue.getYouTrackLink())
        val cellStyle = if (value.issue.resolutionDate != null) workbook.resolvedIssueStyle else workbook.regularIssueStyle

        cell.hyperlink = link
        cell.setCellValue(value.getLabel())
        cell.cellStyle = cellStyle
    }

    private fun renderSummary(workbook: POIWorkbook, cell: Cell, value: SummaryTreeTableRow) {
        cell.cellStyle = workbook.issueSummaryStyle
        cell.setCellValue(value.getLabel())
    }
}
