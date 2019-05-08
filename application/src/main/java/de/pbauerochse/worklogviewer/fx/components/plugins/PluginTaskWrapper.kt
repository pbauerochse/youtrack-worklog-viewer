package de.pbauerochse.worklogviewer.fx.components.plugins

import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.plugins.tasks.PluginTask
import de.pbauerochse.worklogviewer.tasks.Progress

class PluginTaskWrapper<T>(private val task: PluginTask<T>) : WorklogViewerTask<T?>(task.label) {

    override fun start(progress: Progress): T? {
        return try {
            task.run(progress)
        } catch (e : Exception) {
            exception = e
            null
        }
    }
}
