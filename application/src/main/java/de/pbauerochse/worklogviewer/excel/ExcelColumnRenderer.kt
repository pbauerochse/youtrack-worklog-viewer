package de.pbauerochse.worklogviewer.excel

import de.pbauerochse.worklogviewer.timereport.view.ReportRow

internal interface ExcelColumnRenderer {

    val headline: String
    fun write(context: ExcelRenderContext, columnIndex: Int, reportRow: ReportRow)

}