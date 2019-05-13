package de.pbauerochse.worklogviewer.excel

import de.pbauerochse.worklogviewer.report.view.ReportRow

internal interface ExcelColumnRenderer {

    val headline: String
    fun write(excelRow: POIRow, columnIndex: Int, reportRow: ReportRow)

}