package de.pbauerochse.worklogviewer.excel.columns

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer
import de.pbauerochse.worklogviewer.excel.POIRow
import de.pbauerochse.worklogviewer.excel.setTimeSpent
import de.pbauerochse.worklogviewer.fx.components.treetable.TreeTableRowModel
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

/**
 * Renders the last summary column
 */
class SummaryExcelColumn : ExcelColumnRenderer {

    override val headline: String = getFormatted("view.main.summary")

    override fun write(row: POIRow, columnIndex: Int, value: TreeTableRowModel) {
        val cell = row.createCell(columnIndex)
        val workbook = row.sheet.workbook

        val timeSpentInMinutes = value.getTotalTimeSpent()
        cell.setTimeSpent(timeSpentInMinutes)
        cell.cellStyle = if (value.isGroupByRow) workbook.groupByTimeSpentStyle else workbook.issueSummaryStyle
    }

}
