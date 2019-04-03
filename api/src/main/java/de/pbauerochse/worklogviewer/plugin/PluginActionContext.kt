package de.pbauerochse.worklogviewer.plugin

import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.tasks.AsyncTask
import java.io.File
import java.net.URL

interface PluginActionContext {
    val http : Http
    val connector : YouTrackConnector?
    val currentTimeReport : TimeReport?
    val currentlyVisibleIssues : TabContext?

    fun <T> triggerTask(task : AsyncTask<T>, callback : ((T?) -> Unit)? = null)
    fun showInPopup(fxmlUrl : URL, specs : PopupSpecification)
    fun showSaveFileDialog(spec : FileChooserSpec) : File?
    fun showOpenFileDialog(spec : FileChooserSpec) : File?

}