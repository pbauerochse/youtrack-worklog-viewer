package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.fx.dialog.Dialog
import de.pbauerochse.worklogviewer.logging.LogMessageListener
import de.pbauerochse.worklogviewer.logging.WorklogViewerLogs
import de.pbauerochse.worklogviewer.plugins.dialog.FileChooserSpecification
import de.pbauerochse.worklogviewer.plugins.dialog.FileType
import de.pbauerochse.worklogviewer.settings.WorklogViewerFiles
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ListView
import javafx.stage.Window
import javafx.stage.WindowEvent
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*

/**
 * Controller for the LogMessages view. Stores the retrieved Log Messages
 * and displays them in an appropriate FX component.
 *
 * The component only gets updated when visible
 */
class LogViewController : Initializable, LogMessageListener {

    @FXML
    private lateinit var logMessagesComponent: ListView<String>

    private val modalCloseEventListener = EventHandler<WindowEvent> { WorklogViewerLogs.removeListener(this) }
    private val windowChangeListener = ChangeListener<Window> { _, oldValue, newValue ->
        oldValue?.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, modalCloseEventListener)
        newValue?.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, modalCloseEventListener)
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        LOGGER.debug("Initializing")
        logMessagesComponent.sceneProperty().addListener { _, oldValue, newValue ->
            oldValue?.let {
                LOGGER.debug("Removing WindowChangeListener")
                it.windowProperty().removeListener(windowChangeListener)
            }

            newValue?.let {
                LOGGER.debug("Adding WindowChangeListener")
                it.windowProperty().addListener(windowChangeListener)
            }
        }

        WorklogViewerLogs.addListener(this)
        val pendingLogMessages = WorklogViewerLogs.getRecentLogMessages()
        LOGGER.debug("Adding ${pendingLogMessages.size} Log messages to component")
        addLogMessages(pendingLogMessages)
    }

    override fun onLogMessage(messages: List<String>) {
        // important: do not log anything in here otherwise you will get an infinite logging loop
        addLogMessages(messages)
    }

    fun showSaveLogFileDialog() {
        val specification = FileChooserSpecification(
            getFormatted("view.logs.savefile"),
            "worklog-viewer.log",
            FileType("Worklog Viewer Log File", "*.log")
        )
        Dialog(logMessagesComponent.scene).showSaveFileDialog(specification) { copyLogFileTo(it) }
    }

    private fun copyLogFileTo(targetFile: File) {
        try {
            Files.copy(WorklogViewerFiles.LOG_FILE.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } catch (e: IOException) {
            throw ExceptionUtil.getIllegalArgumentException("exceptions.logs.savefile", e, targetFile.absolutePath)
        }
    }

    private fun addLogMessages(messages: List<String>) {
        Platform.runLater {
            logMessagesComponent.items.addAll(messages)
            logMessagesComponent.scrollTo(logMessagesComponent.items.size - 1)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(LogViewController::class.java)
    }
}
