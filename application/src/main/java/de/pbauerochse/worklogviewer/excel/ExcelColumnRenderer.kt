package de.pbauerochse.worklogviewer.excel

import de.pbauerochse.worklogviewer.fx.components.treetable.data.TimeReportRowModel

internal interface ExcelColumnRenderer {

    val headline: String

    fun write(row: POIRow, columnIndex: Int, value: TimeReportRowModel)

}