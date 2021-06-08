package de.pbauerochse.worklogviewer.tasks

interface TaskPreparer {

    /**
     * Allows customizing the [WorklogViewerTask] by adding
     * action handlers
     */
    fun <T> prepareTaskForExecution(task: WorklogViewerTask<T>): WorklogViewerTask<T>

}