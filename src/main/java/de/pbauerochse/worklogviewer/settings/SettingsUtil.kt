package de.pbauerochse.worklogviewer.settings

import de.pbauerochse.worklogviewer.settings.loaders.JsonSettingsLoader
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
    private val SETTINGS_JSON_FILE = File(System.getProperty("user.home"), ".youtrack-worklog-viewer.json")

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
        if (needsMigrationOfOldSettingsFile()) {
            migrateOldSettingsToNewSettings()
        }

        return JsonSettingsLoader(SETTINGS_JSON_FILE).load()
    }

    private fun needsMigrationOfOldSettingsFile(): Boolean = OLD_SETTINGS_PROPERTIES_FILE.exists() && !SETTINGS_JSON_FILE.exists()

    private fun migrateOldSettingsToNewSettings() {
        SettingsMigrator(OLD_SETTINGS_PROPERTIES_FILE).migrateTo(SETTINGS_JSON_FILE)
    }

}
