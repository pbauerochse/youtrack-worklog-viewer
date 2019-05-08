package de.pbauerochse.worklogviewer.fx.plugins

import de.pbauerochse.worklogviewer.plugins.actions.PluginActionContext
import de.pbauerochse.worklogviewer.plugins.state.WorklogViewerState
import de.pbauerochse.worklogviewer.plugins.tools.WorklogViewerTools

class PluginActionContextAdapter : PluginActionContext {
    override val state: WorklogViewerState
        get() = WorklogViewerStateAdapter()

    override val tools: WorklogViewerTools
        get() = WorklogViewerToolsAdapter()


//    override val http: Http
//        get() = Http(settingsModel.settings.youTrackConnectionSettings)
//
//    override val connector: YouTrackConnector?
//        get() = YouTrackConnectorLocator.getActiveConnector()
//
//    override var currentTimeReport: TimeReport? = null
//
//    override val currentlyVisibleIssues: TabContext?
//        get() = resultTabPane.currentlyVisibleTab.currentData?.let {
//            TabContext(it.reportParameters, it.issues)
//        }
//
//    override val timesFormatter: WorktimeFormatter
//        get() = WorklogTimeFormatter(settingsModel.workhoursProperty.get())
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T> triggerTask(task: AsyncTask<T>, callback: ((T?) -> Unit)?) {
//        val pluginTask = PluginTask(task)
//        pluginTask.setOnSucceeded { callback?.invoke(it.source.value as T?) }
//        taskRunner.startTask(pluginTask)
//    }
//
//    override fun showInPopup(fxmlUrl: URL, specs: PopupSpecification) {
//        mainToolbar.scene.openDialog(fxmlUrl, specs)
//    }
//
//    override fun showSaveFileDialog(spec: FileChooserSpec): File? {
//        return fileChooser(spec).showSaveDialog(resultTabPane.scene.window)
//    }
//
//    override fun showOpenFileDialog(spec: FileChooserSpec): File? {
//        return fileChooser(spec).showOpenDialog(resultTabPane.scene.window)
//    }

//    private fun fileChooser(spec: FileChooserSpecification): FileChooser {
//        return FileChooser().apply {
//            title = spec.title
//            initialFileName = spec.initialFileName
//            selectedExtensionFilter = spec.fileType?.let {
//                FileChooser.ExtensionFilter(it.description, it.extension)
//            }
//        }
//    }
}