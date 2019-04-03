package de.pbauerochse.worklogviewer.settings

import java.io.File

object WorklogViewerFiles {

    val USER_HOME = File(System.getProperty("user.home"))
    val WORKLOG_VIEWER_HOME = File(USER_HOME, ".youtrack-worklog-viewer")

    val OLD_SETTINGS_PROPERTIES_FILE = File(USER_HOME, "youtrack-worklog.properties")
    val OLD_SETTINGS_JSON_FILE = File(USER_HOME, ".youtrack-worklog-viewer.json")
    val SETTINGS_JSON_FILE = File(WORKLOG_VIEWER_HOME, "settings.json")

    @JvmField
    val LOG_FILE = File(WORKLOG_VIEWER_HOME, "logs/worklog-viewer.log")

}