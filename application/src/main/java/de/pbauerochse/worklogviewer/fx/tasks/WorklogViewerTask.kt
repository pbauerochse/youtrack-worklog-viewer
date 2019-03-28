package de.pbauerochse.worklogviewer.fx.tasks

import de.pbauerochse.worklogviewer.tasks.ProgressCallback
import javafx.concurrent.Task

abstract class WorklogViewerTask<T> : Task<T>(), ProgressCallback {

    init {
        this.updateTitle(this.label)
    }

    /**
     * The display label of this Task.
     * Will be shown in the corresponding
     * [TaskProgressBar]
     */
    abstract val label: String

    abstract fun start(progressCallback: ProgressCallback): T

    override fun call(): T = start(this)

    override fun setProgress(message: String, amount: Double) {
        updateProgress(amount, 100.toDouble())
        updateMessage(message)
    }
}