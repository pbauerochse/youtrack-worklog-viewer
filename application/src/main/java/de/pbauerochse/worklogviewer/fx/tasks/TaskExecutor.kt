package de.pbauerochse.worklogviewer.fx.tasks

import java.util.concurrent.Future

interface TaskExecutor {

    fun <T> startTask(task: WorklogViewerTask<T>): Future<T>

}