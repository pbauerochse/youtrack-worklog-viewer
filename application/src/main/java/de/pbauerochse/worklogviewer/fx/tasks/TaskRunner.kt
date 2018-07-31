package de.pbauerochse.worklogviewer.fx.tasks

import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.concurrent.Task
import javafx.css.Styleable
import javafx.scene.control.ProgressBar
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Starts async Tasks and sets the UI state
 * according to the Task state
 */
class TaskRunner(
    private val progressText: Text,
    private val progressBar: ProgressBar,
    private val waitScreenOverlay: StackPane
) {

    /**
     * Starts a thread performing the given task
     * @param task The task to perform
     */
    fun runTask(task: Task<*>) {
        LOGGER.info("Starting task {}", task.title)
        bindOnRunning(task)
        bindOnSucceeded(task)
        bindOnFailed(task)

        // state change listener just for logging purposes
        task.stateProperty().addListener { _, oldValue, newValue -> LOGGER.debug("Task {} changed from {} to {}", task.title, oldValue, newValue) }

        EXECUTOR.submit(task)
    }

    /**
     * While the task is running, the waitScreenOverlay
     * will be shown and the progress bar and the text bound
     * to the Task
     */
    private fun bindOnRunning(task: Task<*>) {
        val initialOnRunningHandler = task.onRunning
        task.setOnRunning { event ->
            waitScreenOverlay.isVisible = true
            progressText.textProperty().bind(task.messageProperty())
            progressBar.progressProperty().bind(task.progressProperty())

            setStyle(RUNNING_CLASS, progressBar)
            setStyle(RUNNING_CLASS, progressText)

            initialOnRunningHandler?.handle(event)
        }
    }

    private fun bindOnSucceeded(task: Task<*>) {
        val initialOnSucceededHandler = task.onSucceeded
        task.setOnSucceeded { event ->
            LOGGER.info("Task {} succeeded", task.title)

            // unbind progress indicators
            progressText.textProperty().unbind()
            progressBar.progressProperty().unbind()

            setStyle(SUCCESSFUL_CLASS, progressBar)
            setStyle(SUCCESSFUL_CLASS, progressText)

            if (initialOnSucceededHandler != null) {
                LOGGER.debug("Delegating Event to previous onSucceeded event handler")
                initialOnSucceededHandler.handle(event)
            }

            waitScreenOverlay.isVisible = false
        }
    }

    private fun bindOnFailed(task: Task<*>) {
        val initialOnFailedHandler = task.onFailed
        task.setOnFailed { event ->
            LOGGER.warn("Task {} failed", task.title)

            // unbind progress indicators
            progressText.textProperty().unbind()
            progressBar.progressProperty().unbind()

            setStyle(ERROR_CLASS, progressBar)
            setStyle(ERROR_CLASS, progressText)

            if (initialOnFailedHandler != null) {
                LOGGER.debug("Delegating Event to previous onFailed event handler")
                initialOnFailedHandler.handle(event)
            }

            val throwable = event.source.exception
            if (throwable != null && StringUtils.isNotBlank(throwable.message)) {
                LOGGER.warn("Showing error to user", throwable)
                progressText.text = throwable.message
            } else {
                if (throwable != null) {
                    LOGGER.warn("Error executing task {}", task.toString(), throwable)
                }

                progressText.text = FormattingUtil.getFormatted("exceptions.main.worker.unknown")
            }

            progressBar.progress = 1.0
            waitScreenOverlay.isVisible = false
        }
    }

    private fun setStyle(style: String, stylable: Styleable) {
        stylable.styleClass.removeAll(ERROR_CLASS, RUNNING_CLASS, SUCCESSFUL_CLASS)
        stylable.styleClass.add(style)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TaskRunner::class.java)

        private const val ERROR_CLASS = "error"
        private const val RUNNING_CLASS = "running"
        private const val SUCCESSFUL_CLASS = "success"

        @JvmStatic
        val EXECUTOR = ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, LinkedBlockingQueue())
    }

}