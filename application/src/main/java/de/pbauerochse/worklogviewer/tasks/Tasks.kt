package de.pbauerochse.worklogviewer.tasks

import de.pbauerochse.worklogviewer.fx.tasks.TaskExecutor
import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import java.util.concurrent.Future

object Tasks: TaskExecutor {
    var delegate: TaskExecutor = DefaultTaskExecutor

    override fun <T> startBackgroundTask(task: WorklogViewerTask<T>): Future<T> = delegate.startBackgroundTask(task)
    override fun <T> startTask(task: WorklogViewerTask<T>): Future<T> = delegate.startTask(task)
}