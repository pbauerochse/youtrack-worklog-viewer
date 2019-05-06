package de.pbauerochse.worklogviewer.fx.components.plugins

import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.tasks.AsyncTask
import de.pbauerochse.worklogviewer.tasks.Progress

class PluginTask<T>(private val tasklet: AsyncTask<T>) : WorklogViewerTask<T?>(tasklet.label) {

    override fun start(progress: Progress): T? {
        return try {
            tasklet.run(progress)
        } catch (e : Exception) {
            exception = e
            null
        }
    }
}
