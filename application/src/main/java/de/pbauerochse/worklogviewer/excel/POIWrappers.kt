package de.pbauerochse.worklogviewer.excel

import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.ss.usermodel.*
import java.net.URL
import kotlin.math.max

/**
 * Provides helper utilities for
 * working with POI Workbooks
 */
class POIWorkbook(internal val workbook: Workbook) {

    fun createSheet(text: String): POISheet = POISheet(workbook.createSheet(text), this)

    val headlineStyle: CellStyle by lazy {
        workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 14
            })
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.BOTTOM
        }
    }

    val groupByHeadlineStyle: CellStyle by lazy {
        workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 12
            })
            alignment = HorizontalAlignment.LEFT
            verticalAlignment = VerticalAlignment.BOTTOM
        }
    }

    val groupByTimeSpentStyle: CellStyle by lazy {
        workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 12
            })
            alignment = HorizontalAlignment.RIGHT
            verticalAlignment = VerticalAlignment.BOTTOM
        }
    }

    val resolvedIssueStyle: CellStyle by lazy {
        workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                fontHeightInPoints = 10
                strikeout = true
                color = IndexedColors.LIGHT_BLUE.getIndex()
            })
            alignment = HorizontalAlignment.LEFT
            verticalAlignment = VerticalAlignment.CENTER
            fillForegroundColor = IndexedColors.LIGHT_BLUE.getIndex()
        }
    }

    val regularIssueStyle: CellStyle by lazy {
        workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                fontHeightInPoints = 10
                color = IndexedColors.BLUE.getIndex()
            })
            alignment = HorizontalAlignment.LEFT
            verticalAlignment = VerticalAlignment.CENTER
            fillForegroundColor = IndexedColors.BLUE.getIndex()
        }
    }

    val issueSummaryStyle: CellStyle by lazy {
        workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                fontHeightInPoints = 10
                bold = true
            })
            alignment = HorizontalAlignment.RIGHT
            verticalAlignment = VerticalAlignment.CENTER
        }
    }

    val issueTimeSpentStyle: CellStyle by lazy {
        workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                fontHeightInPoints = 10
            })
            alignment = HorizontalAlignment.RIGHT
            verticalAlignment = VerticalAlignment.CENTER
        }
    }

    fun createHyperlink(url: URL): Hyperlink {
        val link = workbook.creationHelper.createHyperlink(HyperlinkType.URL)
        link.address = url.toExternalForm()
        return link
    }

}

class POISheet(private val sheet: Sheet, val workbook: POIWorkbook) {

    private var nextRowIncrement = 1
    private var maxCellIndex = 0

    fun createNextRow(): POIRow {
        val lastRowNum = sheet.lastRowNum
        val nextRowNum = lastRowNum + nextRowIncrement
        nextRowIncrement = 1

        return POIRow(sheet.createRow(nextRowNum), this)
    }

    /**
     * Can be called if additional spacing
     * between the current Row and the Row
     * returned by #createNextRow is desired
     */
    fun addSpacing(numRows: Int) {
        nextRowIncrement = max(1, max(numRows + 1, nextRowIncrement))
    }

    fun autoSizeColumns() {
        (0..maxCellIndex).forEach {
            sheet.autoSizeColumn(it)
        }
    }

    internal fun createdCellAt(num: Int) {
        maxCellIndex = max(0, max(num, maxCellIndex))
    }

    fun writeHeadlines(headlines: List<String>) {
        val row = createNextRow()
        headlines.forEachIndexed { index, headline ->
            val cell = row.createCell(index)
            cell.cellStyle = workbook.headlineStyle
            cell.setCellValue(headline)
        }

        row.adjustHeight()
    }
}

class POIRow(private val row: Row, val sheet: POISheet) {

    fun createCell(columnIndex: Int): Cell {
        sheet.createdCellAt(columnIndex)
        return row.createCell(columnIndex)
    }

    fun adjustHeight() {
        val maxFontHeight = getMaxFontHeightInPointsInRow() ?: 10
        row.heightInPoints = max(maxFontHeight + ADDITIONAL_ROW_HEIGHT, row.heightInPoints)
    }

    private fun getMaxFontHeightInPointsInRow(): Short? {
        return row.cellIterator().asSequence()
            .map { it.cellStyle }
            .filter { it != null }
            .map { it.fontIndexAsInt }
            .map { row.sheet.workbook.getFontAt(it).fontHeightInPoints }
            .maxOrNull()
    }

    companion object {
        private const val ADDITIONAL_ROW_HEIGHT = 5f
    }

}

fun Cell.setTimeSpent(timeSpentInMinutes: Long) {
    if (SettingsUtil.settingsViewModel.showDecimalsInExcelProperty.get()) {
        setCellValue(timeSpentInMinutes.toDouble() / 60.0)
    } else {
        setCellValue(FormattingUtil.formatMinutes(timeSpentInMinutes))
    }
}
