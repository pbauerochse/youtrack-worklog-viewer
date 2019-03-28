package de.pbauerochse.worklogviewer.settings

import de.pbauerochse.worklogviewer.settings.loaders.JsonSettingsLoader
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import java.io.File

/**
 * Loads and stores the Settings object
 * in a properties file.
 *
 * Can also be used, to obtain the singleton instance
 * of the Settings or SettingsViewModel instance
 */
object SettingsUtil {

    private val OLD_SETTINGS_PROPERTIES_FILE = File(System.getProperty("user.home"), "youtrack-worklog.properties")
    private val OLD_SETTINGS_JSON_FILE = File(System.getProperty("user.home"), ".youtrack-worklog-viewer.json")
    private val SETTINGS_JSON_FILE = File(System.getProperty("user.home"), ".youtrack-worklog-viewer/settings.json")

    @JvmStatic
    val settings: Settings by lazy {
        loadSettings()
    }

    @JvmStatic
    val settingsViewModel: SettingsViewModel by lazy {
        SettingsViewModel(settings)
    }

    @JvmStatic
    fun saveSettings() {
        JsonSettingsLoader(SETTINGS_JSON_FILE).save(settings)
    }

    private fun loadSettings(): Settings {
        when {
            needsMigrationOfOldSettingsFile() -> migrateOldSettingsToNewSettings()
            needsMovingOfOldJsonSettingsFile() -> moveOldJsonSettingsFile()
        }

        return JsonSettingsLoader(SETTINGS_JSON_FILE).load()
    }

    private fun needsMigrationOfOldSettingsFile(): Boolean = OLD_SETTINGS_PROPERTIES_FILE.exists() && !SETTINGS_JSON_FILE.exists()

    private fun migrateOldSettingsToNewSettings() {
        SettingsMigrator(OLD_SETTINGS_PROPERTIES_FILE).migrateTo(SETTINGS_JSON_FILE)
    }

    private fun needsMovingOfOldJsonSettingsFile(): Boolean = OLD_SETTINGS_JSON_FILE.exists() && SETTINGS_JSON_FILE.exists().not()

    private fun moveOldJsonSettingsFile() {
        val worklogViewerDirectory = SETTINGS_JSON_FILE.parentFile
        if (worklogViewerDirectory.exists().not() && worklogViewerDirectory.mkdirs().not()) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.create", SETTINGS_JSON_FILE.absolutePath)
        }

        check(SETTINGS_JSON_FILE.createNewFile()) { FormattingUtil.getFormatted("exceptions.settings.create", SETTINGS_JSON_FILE.absolutePath) }
        OLD_SETTINGS_JSON_FILE.copyTo(SETTINGS_JSON_FILE, true)
        OLD_SETTINGS_JSON_FILE.delete()
    }

}
