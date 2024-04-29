package de.pbauerochse.worklogviewer.fx.tasks

import de.pbauerochse.worklogviewer.excel.ExcelExporter
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Task the triggers the Excel Report generation
 * of the given data
 */
class ExportToExcelTask(
    private val text: String,
    private val data: ReportView,
    private val targetFile: File
) : WorklogViewerTask<File>(FormattingUtil.getFormatted("task.excelexport", text)) {

    override fun start(progress: Progress): File {
        progress.setProgress(FormattingUtil.getFormatted("worker.excel.exporting"), 20)
        try {
            FileOutputStream(targetFile).use { ExcelExporter.writeReport(text, data, it) }
            progress.setProgress(FormattingUtil.getFormatted("exceptions.excel.success", targetFile.absolutePath), 100)
        } catch (e: IOException) {
            LOG.warn("Could not write Excel to {}", targetFile.absolutePath, e)
            throw ExceptionUtil.getIllegalStateException("exceptions.excel.error", e, targetFile.absolutePath)
        }
        return targetFile
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(ExportToExcelTask::class.java)
    }
}
