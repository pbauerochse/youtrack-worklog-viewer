package de.pbauerochse.worklogviewer.settings

import de.pbauerochse.worklogviewer.trimToNull
import java.io.File

object WorklogViewerFiles {

    private val USER_HOME = File(System.getProperty("user.home"))
    val WORKLOG_VIEWER_HOME: File
        get() {
            val fromParameter = System.getProperty("worklogviewer.home")?.trimToNull()
            return when {
                isValidDirectory(fromParameter) -> File(fromParameter)
                else -> File(USER_HOME, ".youtrack-worklog-viewer")
            }
        }

    val OLD_SETTINGS_PROPERTIES_FILE = File(USER_HOME, "youtrack-worklog.properties")

    val OLD_SETTINGS_JSON_FILE = File(USER_HOME, ".youtrack-worklog-viewer.json")
    val SETTINGS_JSON_FILE = File(WORKLOG_VIEWER_HOME, "settings.json")

    private fun isValidDirectory(fromParameter: String?): Boolean {
        return fromParameter != null && File(fromParameter).isDirectory
    }

}