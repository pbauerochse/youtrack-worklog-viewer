package de.pbauerochse.worklogviewer.tasks

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.Future

object Tasks {

    private val preparers = mutableListOf<TaskPreparer>()

    private val runningForegroundTasksList: ObservableList<WorklogViewerTask<*>> = FXCollections.observableArrayList()
    private val runningBackgroundTasksList: ObservableList<WorklogViewerTask<*>> = FXCollections.observableArrayList()
    val hasRunningForegroundTasks: BooleanBinding = Bindings.isNotEmpty(runningForegroundTasksList)

    private val LOGGER = LoggerFactory.getLogger(Tasks::class.java)
    private val EXECUTOR = Executors.newFixedThreadPool(5)

    fun <T> startBackgroundTask(task: WorklogViewerTask<T>): Future<T> = prepareTaskForExecution(task, runningBackgroundTasksList)
    fun <T> startTask(task: WorklogViewerTask<T>): Future<T> = prepareTaskForExecution(task, runningForegroundTasksList)

    private fun <T> prepareTaskForExecution(task: WorklogViewerTask<T>, taskList: ObservableList<WorklogViewerTask<*>>): Future<T> {
        LOGGER.info("Scheduling Task ${task.label}")

        var taskToExecute = task
        preparers.forEach {
            LOGGER.debug("Applying Preparer $it to Task $taskToExecute")
            taskToExecute = it.prepareTaskForExecution(taskToExecute)
        }

        bindLogging(task)
        bindOnRunning(task, taskList)
        bindOnSucceeded(task, taskList)
        bindOnFailed(task, taskList)

        EXECUTOR.submit(task)
        return task
    }

    private fun <T> bindLogging(task: WorklogViewerTask<T>) {
        task.stateProperty().addListener { _, old, new -> LOGGER.debug("Task ${task.label} changed state from $old to $new") }
    }

    private fun <T> bindOnRunning(task: WorklogViewerTask<T>, taskList: ObservableList<WorklogViewerTask<*>>) {
        val initialHandler = task.onRunning
        task.setOnRunning {
            LOGGER.debug("${task.label} running")
            taskList.add(task)
            initialHandler?.handle(it)
        }
    }

    private fun <T> bindOnSucceeded(task: WorklogViewerTask<T>, taskList: ObservableList<WorklogViewerTask<*>>) {
        val initialHandler = task.onSucceeded
        task.setOnSucceeded {
            LOGGER.debug("${task.label} succeeded")
            taskList.remove(task)
            initialHandler?.handle(it)
        }
    }

    private fun <T> bindOnFailed(task: WorklogViewerTask<T>, taskList: ObservableList<WorklogViewerTask<*>>) {
        val initialHandler = task.onFailed
        task.setOnFailed {
            LOGGER.warn("${task.label} failed", it.source.exception)
            taskList.remove(task)
            initialHandler?.handle(it)
        }
    }

    fun registerPreparer(preparer: TaskPreparer) {
        preparers.add(preparer)
    }

    @JvmStatic
    fun shutdown() {
        EXECUTOR.shutdownNow()
    }


}