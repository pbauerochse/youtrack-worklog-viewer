package de.pbauerochse.worklogviewer.fx.tasks

import de.pbauerochse.worklogviewer.tasks.Progress
import javafx.concurrent.Task

abstract class WorklogViewerTask<T>(val label: String) : Task<T>() {

    init {
        this.updateTitle(label)
    }

    abstract fun start(progress: Progress): T

    override fun call(): T {
        val progress = ProgressHandler()
        progress.currentMessageProperty().addListener { _, _, newValue -> updateMessage(newValue) }
        progress.currentValueProperty().addListener { _, _, newValue -> updateProgress(newValue.toDouble(), 100.0) }

        return start(progress)
    }
}