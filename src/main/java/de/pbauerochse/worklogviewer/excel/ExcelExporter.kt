package de.pbauerochse.worklogviewer.excel

import de.pbauerochse.worklogviewer.domain.TimerangeProvider
import de.pbauerochse.worklogviewer.excel.columns.IssueLinkExcelColumn
import de.pbauerochse.worklogviewer.excel.columns.IssueStatusExcelColumn
import de.pbauerochse.worklogviewer.excel.columns.TaskWorklogSummaryExcelColumn
import de.pbauerochse.worklogviewer.excel.columns.WorklogExcelColumn
import de.pbauerochse.worklogviewer.fx.components.treetable.WorklogsTreeTableViewData
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
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
    fun createWorkbook(text: String, data: WorklogsTreeTableViewData): Workbook {
        LOGGER.info("Creating workbook for ${data.issues.size} '$text' Issues")
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet(text)

        renderData(data, sheet)

        return workbook
    }

    private fun renderData(data: WorklogsTreeTableViewData, sheet: Sheet) {
        val cellWriters = getCellWriters(data.reportParameters.timerangeProvider)
        for (treeRow in data.treeRows) {
            var row = sheet.createNextRow()

            cellWriters.forEachIndexed { index, renderer ->
                renderer.write(row, index, treeRow.value)
            }

            treeRow.children.forEach {
                row = sheet.createNextRow()
                cellWriters.forEachIndexed { index, renderer ->
                    renderer.write(row, index, it.value)
                }
            }
        }

        cellWriters.indices.forEach {
            sheet.autoSizeColumn(it)
        }
    }

    private fun getCellWriters(timerangeProvider: TimerangeProvider): List<ExcelColumnRenderer> {
        val startDate = timerangeProvider.startDate
        val endDate = timerangeProvider.endDate
        val renderers = arrayListOf(
            IssueStatusExcelColumn(),
            IssueLinkExcelColumn()
        )

        val daysBetweenStartAndEndDate = ChronoUnit.DAYS
            .between(startDate, endDate)
            .toInt()

        for (days in 0..daysBetweenStartAndEndDate) {
            val currentDate = startDate.plusDays(days.toLong())
            renderers.add(WorklogExcelColumn(currentDate))
        }

        renderers.add(TaskWorklogSummaryExcelColumn())

        return renderers
    }

}