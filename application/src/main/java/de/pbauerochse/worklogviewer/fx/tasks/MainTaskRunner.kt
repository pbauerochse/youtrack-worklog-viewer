package de.pbauerochse.worklogviewer.fx.tasks

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.Future

object MainTaskRunner : TaskExecutor {

    private val runningTasksList: ObservableList<WorklogViewerTask<*>> = FXCollections.observableArrayList()
    val runningTasksProperty: ReadOnlyListProperty<WorklogViewerTask<*>> = SimpleListProperty(runningTasksList)

    private val LOGGER = LoggerFactory.getLogger(MainTaskRunner::class.java)
    private val EXECUTOR = Executors.newFixedThreadPool(5)

    @Suppress("UNCHECKED_CAST")
    override fun <T> startTask(task: WorklogViewerTask<T>): Future<T> {
        LOGGER.info("Scheduling Task ${task.label}")
        bindLogging(task)
        bindOnRunning(task)
        bindOnSucceeded(task)
        bindOnFailed(task)
        return EXECUTOR.submit(task) as Future<T>
    }

    private fun <T> bindLogging(task: WorklogViewerTask<T>) {
        task.stateProperty().addListener { _, old, new -> LOGGER.debug("Task ${task.label} changed state from $old to $new") }
    }

    private fun <T> bindOnRunning(task: WorklogViewerTask<T>) {
        val initialHandler = task.onRunning
        task.setOnRunning {
            LOGGER.debug("${task.label} running")
            runningTasksList.add(task)
            initialHandler?.handle(it)
        }
    }

    private fun <T> bindOnSucceeded(task: WorklogViewerTask<T>) {
        val initialHandler = task.onSucceeded
        task.setOnSucceeded {
            LOGGER.debug("${task.label} succeeded")
            runningTasksList.remove(task)
            initialHandler?.handle(it)
        }
    }

    private fun <T> bindOnFailed(task: WorklogViewerTask<T>) {
        val initialHandler = task.onFailed
        task.setOnFailed {
            LOGGER.debug("${task.label} failed")
            runningTasksList.remove(task)
            initialHandler?.handle(it)
        }
    }

    @JvmStatic
    fun shutdown() {
        EXECUTOR.shutdownNow()
    }


}