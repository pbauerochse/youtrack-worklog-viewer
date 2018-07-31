package de.pbauerochse.worklogviewer.fx.tasks

import de.pbauerochse.worklogviewer.connector.ProgressCallback
import javafx.concurrent.Task

class DelegatingTask<T>(private val task: WorklogViewerTask<T>) : Task<T>(), ProgressCallback {

    override fun call(): T = task.start(this)

    override fun setProgress(message: String, amount: Int) {
        updateProgress(amount.toLong(), 100)
        updateMessage(message)
    }

}