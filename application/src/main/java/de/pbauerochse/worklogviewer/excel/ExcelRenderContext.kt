package de.pbauerochse.worklogviewer.excel

import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import org.dhatim.fastexcel.Color
import org.dhatim.fastexcel.HyperLink
import org.dhatim.fastexcel.StyleSetter
import org.dhatim.fastexcel.Worksheet
import java.net.URL

class ExcelRenderContext(val sheet: Worksheet) {

    var currentRow = 0
        private set

    internal fun writeHeadlines(cellWriters: List<ExcelColumnRenderer>) {
        cellWriters.forEachIndexed { index, excelColumnRenderer ->
            sheet.value(currentRow, index, excelColumnRenderer.headline)
            sheet.style(currentRow, index)
                .fontSize(14)
                .bold()
                .horizontalAlignment("center")
                .verticalAlignment("bottom")
                .set()
        }
    }

    internal fun addSpacing(num: Int) = nextRow(num)

    internal fun nextRow(by: Int = 1) {
        currentRow += by
    }

    fun value(cell: Int, text: String): ExcelRenderContext {
        sheet.value(currentRow, cell, text)
        return this
    }

    fun hyperLink(cell: Int, linkText: String, target: URL): ExcelRenderContext {
        val hyperLink = HyperLink(target.toExternalForm(), linkText)
        sheet.hyperlink(currentRow, cell, hyperLink)
        return this
    }

    fun setTimeSpent(columnIndex: Int, timeSpentInMinutes: Long): ExcelRenderContext {
        if (SettingsUtil.settingsViewModel.showDecimalsInExcelProperty.get()) {
            sheet.value(currentRow, columnIndex, timeSpentInMinutes.toDouble() / 60.0)
        } else {
            sheet.value(currentRow, columnIndex, FormattingUtil.formatMinutes(timeSpentInMinutes))
        }
        return this
    }

    fun style(cell: Int, style: StyleSetter.() -> Unit): ExcelRenderContext {
        style.invoke(sheet.style(currentRow, cell))
        return this
    }

    companion object {
        val groupByHeadlineStyle: StyleSetter.() -> Unit = {
            fontSize(12)
                .bold()
                .horizontalAlignment("left")
                .verticalAlignment("bottom")
                .set()
        }

        val groupByTimeSpentStyle: StyleSetter.() -> Unit = {
            fontSize(12)
                .bold()
                .horizontalAlignment("right")
                .verticalAlignment("bottom")
                .set()
        }

        val resolvedIssueStyle: StyleSetter.() -> Unit = {
            fontSize(10)
                // .strikeout() TODO
                .italic()
                .fontColor(Color.GRAY6) // does not work for Hyperlinks
                .horizontalAlignment("left")
                .verticalAlignment("center")
                .set()
        }

        val regularIssueStyle: StyleSetter.() -> Unit = {
            fontSize(10)
                .fontColor(Color.SEA_BLUE) // does not work for Hyperlinks
                .bold()
                .horizontalAlignment("left")
                .verticalAlignment("center")
                .set()
        }

        val issueSummaryStyle: StyleSetter.() -> Unit = {
            fontSize(10)
                .bold()
                .horizontalAlignment("right")
                .verticalAlignment("center")
                .set()
        }

        val issueTimeSpentStyle: StyleSetter.() -> Unit = {
            fontSize(10)
                .horizontalAlignment("right")
                .verticalAlignment("center")
                .set()
        }
    }
}