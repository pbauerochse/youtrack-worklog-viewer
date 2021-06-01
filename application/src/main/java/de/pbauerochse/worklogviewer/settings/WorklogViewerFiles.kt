package de.pbauerochse.worklogviewer.settings

import de.pbauerochse.worklogviewer.trimToNull
import java.io.File

/**
 * Contains directories an files for locally written
 * data.
 */
object WorklogViewerFiles {

    private val USER_HOME = File(System.getProperty("user.home"))

    /**
     * The directory within the current users home directory,
     * where configurations and logs for the current user
     * are being stored
     */
    val WORKLOG_VIEWER_HOME = File(USER_HOME, ".youtrack-worklog-viewer")

    /**
     * If the worklog viewer was installed via the installer,
     * this property points to the directory, where it was installed to.
     * This might be a place, were multiple users have access to,
     * so any files written to here, should not contain any user
     * specific data.
     *
     * If the worklog viewer was not installed using the installer,
     * this property points to the [WORKLOG_VIEWER_HOME] directory
     */
    val WORKLOG_VIEWER_INSTALLATION_HOME: File
        get() {
            val fromParameter = System.getProperty("worklogviewer.home")?.trimToNull()
            return when {
                isValidDirectory(fromParameter) -> File(fromParameter!!)
                else -> USER_HOME
            }
        }

    val OLD_SETTINGS_PROPERTIES_FILE = File(USER_HOME, "youtrack-worklog.properties")
    val OLD_SETTINGS_JSON_FILE = File(USER_HOME, ".youtrack-worklog-viewer.json")
    val SETTINGS_JSON_FILE = File(WORKLOG_VIEWER_HOME, "settings.json")

    private fun isValidDirectory(fromParameter: String?): Boolean {
        return fromParameter.isNullOrBlank().not() && File(fromParameter!!).isDirectory
    }

}