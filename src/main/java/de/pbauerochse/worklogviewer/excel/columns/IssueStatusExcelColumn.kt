package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.fx.components.treetable.GroupedIssuesTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.IssueTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.SummaryTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.TreeTableRowModel
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row

/**
 * Writes the Issue status into the appropriate
 * Excel column
 */
class IssueStatusExcelColumn : ExcelColumnRenderer() {

    override fun write(row: Row, columnIndex: Int, value: TreeTableRowModel) {
        val cell = row.createCell(columnIndex)

        when {
            value.isGroupByRow -> renderHeadline(cell, value as GroupedIssuesTreeTableRow)
            value.isIssueRow -> renderIssue(cell, value as IssueTreeTableRow)
            value.isSummaryRow -> renderSummary(cell, value as SummaryTreeTableRow)
        }

        adjustRowHeight(cell)
    }

    private fun renderIssue(cell: Cell, tableRow: IssueTreeTableRow) {
        cell.setCellValue(tableRow.getLabel())
        if (tableRow.issue.resolved != null) {
            cell.cellStyle = getResolvedIssueCellStyle(cell.sheet)
        }
    }

    private fun renderSummary(cell: Cell, tableRow: SummaryTreeTableRow) {
        cell.cellStyle = getHeadlineCellStyle(cell.sheet)
        cell.setCellValue(tableRow.getLabel())
    }

    private fun renderHeadline(cell: Cell, tableRow: GroupedIssuesTreeTableRow) {
        cell.cellStyle = getHeadlineCellStyle(cell.sheet)
        cell.setCellValue(tableRow.getLabel())
    }
}
