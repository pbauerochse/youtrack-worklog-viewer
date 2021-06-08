package de.pbauerochse.worklogviewer.tasks

import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.concurrent.Task

abstract class WorklogViewerTask<T>(val label: String) : Task<T>() {

    init {
        this.updateTitle(label)
    }

    override fun call(): T {
        val progress = ProgressHandler()
        progress.currentMessageProperty().addListener { _, _, newValue -> updateMessage(newValue) }
        progress.currentValueProperty().addListener { _, _, newValue -> updateProgress(newValue.toDouble(), 100.0) }

        progress.setProgress(progress.currentMessage, 0.1)
        return try {
            start(progress)
        } finally {
            progress.setProgress(getFormatted("worker.progress.done"), 100.0)
        }
    }

    abstract fun start(progress: Progress): T
}