package de.pbauerochse.worklogviewer.fx.tasks

import de.pbauerochse.worklogviewer.connector.ProgressCallback

interface WorklogViewerTask<T> {

    /**
     * The display label of this Task.
     * Will be shown in the corresponding
     * [TaskProgressBar]
     */
    val label: String

    fun start(progressCallback: ProgressCallback): T

}