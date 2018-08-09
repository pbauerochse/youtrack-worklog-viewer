package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.excel.POIRow
import de.pbauerochse.worklogviewer.excel.POIWorkbook
import de.pbauerochse.worklogviewer.excel.setTimeSpent
import de.pbauerochse.worklogviewer.fx.components.treetable.GroupedIssuesTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.IssueTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.SummaryTreeTableRow
import de.pbauerochse.worklogviewer.fx.components.treetable.TreeTableRowModel
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

    override fun write(row: POIRow, columnIndex: Int, value: TreeTableRowModel) {
        val cell = row.createCell(columnIndex)
        val workbook = row.sheet.workbook

        when {
            value.isGroupByRow -> renderGroupBySummary(workbook, cell, value as GroupedIssuesTreeTableRow)
            value.isIssueRow -> renderIssueSummary(workbook, cell, value as IssueTreeTableRow)
            value.isSummaryRow -> renderSummary(workbook, cell, value as SummaryTreeTableRow)
        }
    }

    private fun renderGroupBySummary(workbook: POIWorkbook, cell: Cell, value: GroupedIssuesTreeTableRow) {
        val totalTimeSpentInMinutes = value.totalTimeSpentOn(date)
        if (totalTimeSpentInMinutes > 0) {
            cell.setTimeSpent(totalTimeSpentInMinutes)
            cell.cellStyle = workbook.groupByTimeSpentStyle
        }
    }

    private fun renderIssueSummary(workbook: POIWorkbook, cell: Cell, value: IssueTreeTableRow) {
        val timeSpentInMinutes = value.issue.getTimeSpentOn(date)
        if (timeSpentInMinutes > 0) {
            cell.setTimeSpent(timeSpentInMinutes)
            cell.cellStyle = workbook.issueTimeSpentStyle
        }
    }

    private fun renderSummary(workbook: POIWorkbook, cell: Cell, value: SummaryTreeTableRow) {
        val totalTimeSpentInMinutes = value.getTotalTimeSpentOn(date)
        if (totalTimeSpentInMinutes > 0) {
            cell.setTimeSpent(totalTimeSpentInMinutes)
            cell.cellStyle = workbook.issueSummaryStyle
        }
    }

}
