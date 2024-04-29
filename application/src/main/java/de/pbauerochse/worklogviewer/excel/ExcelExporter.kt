package de.pbauerochse.worklogviewer.excel

import de.pbauerochse.worklogviewer.excel.columns.IssueLinkExcelColumn
import de.pbauerochse.worklogviewer.excel.columns.IssueTimeSpentExcelColumn
import de.pbauerochse.worklogviewer.excel.columns.SummaryExcelColumn
import de.pbauerochse.worklogviewer.timereport.TimeRange
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import org.dhatim.fastexcel.Workbook
import org.slf4j.LoggerFactory
import java.io.OutputStream
import java.time.temporal.ChronoUnit

/**
 * Creates an excel workbook out
 * of the report data
 */
object ExcelExporter {

    private val LOGGER = LoggerFactory.getLogger(ExcelExporter::class.java)

    @JvmStatic
    fun writeReport(text: String, data: ReportView, outputStream: OutputStream) {
        LOGGER.info("Creating workbook for ${data.issues.size} '$text' Issues")
        Workbook(outputStream, "YouTrack Worklog Viewer", null).use {
            val sheet = it.newWorksheet(text)
            val context = ExcelRenderContext(sheet)
            renderData(data, context)
        }
    }

    private fun renderData(data: ReportView, context: ExcelRenderContext) {
        val cellWriters = getCellWriters(data.reportParameters.timerange)
        context.writeHeadlines(cellWriters)

        data.rows.forEach { dataRow ->
            context.nextRow()

            cellWriters.forEachIndexed { cellIndex, columnRenderer ->
                columnRenderer.write(context, cellIndex, dataRow)
            }

            if (dataRow.isGrouping) {
                dataRow.children.forEach { childRow ->
                    context.nextRow()
                    cellWriters.forEachIndexed { cellIndex, innerColumnRenderer ->
                        innerColumnRenderer.write(context, cellIndex, childRow)
                    }
                }

                // add spacing
                context.addSpacing(2)
            }
        }
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
