package de.pbauerochse.worklogviewer.fx.components.plugins

import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.tasks.AsyncTask
import de.pbauerochse.worklogviewer.tasks.ProgressCallback

class PluginTask<T>(private val tasklet: AsyncTask<T>) : WorklogViewerTask<T?>() {

    override val label: String = tasklet.label
    override fun start(progressCallback: ProgressCallback): T? {
        return try {
            tasklet.run(progressCallback)
        } catch (e : Exception) {
            exception = e
            null
        }
    }
}
