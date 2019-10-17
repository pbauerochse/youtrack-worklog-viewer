package de.pbauerochse.worklogviewer.excel

import de.pbauerochse.worklogviewer.excel.columns.IssueLinkExcelColumn
import de.pbauerochse.worklogviewer.excel.columns.IssueTimeSpentExcelColumn
import de.pbauerochse.worklogviewer.excel.columns.SummaryExcelColumn
import de.pbauerochse.worklogviewer.report.TimeRange
import de.pbauerochse.worklogviewer.report.view.ReportView
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.slf4j.LoggerFactory
import java.time.temporal.ChronoUnit

/**
 * Creates an excel workbook out
 * of the report data
 */
object ExcelExporter {

    private val LOGGER = LoggerFactory.getLogger(ExcelExporter::class.java)

    @JvmStatic
    fun createWorkbook(text: String, data: ReportView): Workbook {
        LOGGER.info("Creating workbook for ${data.issues.size} '$text' Issues")
        val workbookWrapper = POIWorkbook(HSSFWorkbook())
        val sheet = workbookWrapper.createSheet(text)

        renderData(data, sheet)

        return workbookWrapper.workbook
    }

    private fun renderData(data: ReportView, sheet: POISheet) {
        val cellWriters = getCellWriters(data.reportParameters.timerange)
        sheet.writeHeadlines(cellWriters.map { it.headline })

        data.rows.forEach { dataRow ->
            var excelRow = sheet.createNextRow()

            cellWriters.forEachIndexed { index, columnRenderer ->
                columnRenderer.write(excelRow, index, dataRow)
            }
            excelRow.adjustHeight()

            if (dataRow.isGrouping) {
                dataRow.children.forEach { childRow ->
                    excelRow = sheet.createNextRow()
                    cellWriters.forEachIndexed { innerIndex, innerColumnRenderer ->
                        innerColumnRenderer.write(excelRow, innerIndex, childRow)
                    }
                    excelRow.adjustHeight()
                }

                sheet.addSpacing(2)
            }
        }

        sheet.autoSizeColumns()
    }


    private fun getCellWriters(timeRange: TimeRange): List<ExcelColumnRenderer> {
        val startDate = timeRange.start
        val endDate = timeRange.end

        val renderers = arrayListOf<ExcelColumnRenderer>(IssueLinkExcelColumn())

        val daysBetweenStartAndEndDate = ChronoUnit.DAYS
            .between(startDate, endDate)
            .toInt()

        for (days in 0..daysBetweenStartAndEndDate) {
            val currentDate = startDate.plusDays(days.toLong())
            renderers.add(IssueTimeSpentExcelColumn(currentDate))
        }

        renderers.add(SummaryExcelColumn())

        return renderers
    }

}