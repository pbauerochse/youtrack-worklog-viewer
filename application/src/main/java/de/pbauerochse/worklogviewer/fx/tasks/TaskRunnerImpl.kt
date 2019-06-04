package de.pbauerochse.worklogviewer.fx.tasks

import de.pbauerochse.worklogviewer.plugins.tasks.PluginTask
import de.pbauerochse.worklogviewer.plugins.tasks.TaskCallback
import de.pbauerochse.worklogviewer.plugins.tasks.TaskRunner
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.css.Styleable
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Starts async Tasks and sets the UI state
 * according to the Task state
 */
class TaskRunnerImpl(
        private val progressbarContainer: Pane,
        private val waitScreenOverlay: StackPane,
        private val showTaskNameLabel: Boolean = true
) : TaskRunner {

    private val runningTasks : MutableList<WorklogViewerTask<*>> = mutableListOf()

    @Suppress("UNCHECKED_CAST")
    override fun <T> start(task: PluginTask<T>, callback: TaskCallback<T>?) {
        val pluginTask = object : WorklogViewerTask<T?>(task.label, task.isBlockingUi) {
            override fun start(progress: Progress): T? = task.run(progress)
        }
        pluginTask.setOnSucceeded { callback?.invoke(it.source.value as T?) }
        startTask(pluginTask)
    }

    /**
     * Starts a thread performing the given task
     * @param task The task to perform
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> startTask(task: WorklogViewerTask<T>): Future<T> {
        LOGGER.info("Starting task ${task.label}")
        val progressBar = TaskProgressBar(task, showTaskNameLabel)
        task.stateProperty().addListener(progressBar)

        bindOnRunning(task, progressBar)
        bindOnSucceeded(task, progressBar)
        bindOnFailed(task, progressBar)

        task.stateProperty().addListener { _, oldValue, newValue -> LOGGER.debug("Task ${task.label} changed from $oldValue to $newValue") }

        progressbarContainer.children.add(progressBar)
        return EXECUTOR.submit(task) as Future<T>
    }

    /**
     * While the task is running, the waitScreenOverlay
     * will be shown and the progress bar and the text bound
     * to the Task
     */
    private fun bindOnRunning(task: WorklogViewerTask<*>, progressBar: TaskProgressBar) {
        val initialOnRunningHandler = task.onRunning
        task.setOnRunning { event ->
            LOGGER.info("onRunning: ${task.label}")
            runningTasks.add(task)
            waitScreenOverlay.isVisible = waitScreenOverlay.isVisible || task.isUiBlocking
            progressBar.progressText.textProperty().bind(task.messageProperty())
            progressBar.progressBar.progressProperty().bind(task.progressProperty())

            setStyle(RUNNING_CLASS, progressBar.progressBar)
            setStyle(RUNNING_CLASS, progressBar.progressText)

            initialOnRunningHandler?.handle(event)
        }
    }

    private fun bindOnSucceeded(task: WorklogViewerTask<*>, progressBar: TaskProgressBar) {
        val initialOnSucceededHandler = task.onSucceeded
        task.setOnSucceeded { event ->
            LOGGER.info("Task {} succeeded", task.title)
            runningTasks.remove(task)

            // unbind progress indicators
            progressBar.progressText.textProperty().unbind()
            progressBar.progressBar.progressProperty().unbind()

            setStyle(SUCCESSFUL_CLASS, progressBar.progressBar)
            setStyle(SUCCESSFUL_CLASS, progressBar.progressText)

            if (initialOnSucceededHandler != null) {
                LOGGER.debug("Delegating Event to previous onSucceeded event handler")
                initialOnSucceededHandler.handle(event)
            }

            waitScreenOverlay.isVisible = stillHasRunningUiBlockingTasks()
        }
    }

    private fun bindOnFailed(task: WorklogViewerTask<*>, progressBar: TaskProgressBar) {
        val initialOnFailedHandler = task.onFailed
        task.setOnFailed { event ->
            LOGGER.warn("Task {} failed", task.title)
            runningTasks.remove(task)

            // unbind progress indicators
            progressBar.progressText.textProperty().unbind()
            progressBar.progressBar.progressProperty().unbind()

            setStyle(ERROR_CLASS, progressBar.progressBar)
            setStyle(ERROR_CLASS, progressBar.progressText)

            if (initialOnFailedHandler != null) {
                LOGGER.debug("Delegating Event to previous onFailed event handler")
                initialOnFailedHandler.handle(event)
            }

            val throwable = event.source.exception
            if (throwable != null && throwable.message.isNullOrBlank().not()) {
                LOGGER.warn("Showing error to user", throwable)
                progressBar.progressText.text = throwable.message
            } else {
                if (throwable != null) {
                    LOGGER.warn("Error executing task {}", task.toString(), throwable)
                }

                progressBar.progressText.text = FormattingUtil.getFormatted("exceptions.main.worker.unknown")
            }

            progressBar.progressBar.progress = 1.0
            waitScreenOverlay.isVisible = stillHasRunningUiBlockingTasks()
        }
    }

    private fun setStyle(style: String, stylable: Styleable) {
        stylable.styleClass.removeAll(ERROR_CLASS, RUNNING_CLASS, SUCCESSFUL_CLASS)
        stylable.styleClass.add(style)
    }

    private fun stillHasRunningUiBlockingTasks() : Boolean {
        return runningTasks.any { it.isUiBlocking }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TaskRunnerImpl::class.java)

        private const val ERROR_CLASS = "error"
        private const val RUNNING_CLASS = "running"
        private const val SUCCESSFUL_CLASS = "success"

        private val EXECUTOR = Executors.newFixedThreadPool(5)

        @JvmStatic
        fun shutdownNow() {
            EXECUTOR.shutdownNow()
        }
    }

}
