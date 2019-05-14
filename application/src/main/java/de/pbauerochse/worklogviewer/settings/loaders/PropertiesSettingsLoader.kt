package de.pbauerochse.worklogviewer.settings.loaders

import de.pbauerochse.worklogviewer.settings.Settings
import de.pbauerochse.worklogviewer.timerange.TimerangeProviders
import de.pbauerochse.worklogviewer.util.EncryptionUtil
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.net.MalformedURLException
import java.net.URL
import java.security.GeneralSecurityException
import java.time.DayOfWeek
import java.util.*

/**
 * Loads the old properties settings
 * file into an Settings object
 *
 */
@Deprecated("Properties file is not supported anymore. JSON configuration file is used instead. This loader will be removed soon")
class PropertiesSettingsLoader(private val propertiesFile: File) {

    fun load(): Settings {
        val properties = loadPropertiesFile()
        return fromProperties(properties)
    }

    private fun loadPropertiesFile(): Properties {
        LOGGER.debug("Loading configuration from {}", propertiesFile.absolutePath)
        val properties = Properties()

        try {
            FileInputStream(propertiesFile).use { inputStream -> properties.load(inputStream) }
        } catch (e: IOException) {
            LOGGER.error("Could not read settings from {}", propertiesFile.absolutePath, e)
            throw ExceptionUtil.getRuntimeException("exceptions.settings.read", e, propertiesFile.absolutePath)
        }

        return properties
    }

    private fun fromProperties(properties: Properties): Settings {
        val settings = Settings()

        applyWindowSettings(settings, properties)
        applyYouTrackConnectionSettings(settings, properties)
        applyGeneralSettings(settings, properties)

        return settings
    }

    private fun applyYouTrackConnectionSettings(settings: Settings, properties: Properties) {
        // youtrack version
        val urlProperty = properties.getProperty(YOUTRACK_URL_PROPERTY)
        try {
            settings.youTrackConnectionSettings.baseUrl = URL(urlProperty)
        } catch (e: MalformedURLException) {
            LOGGER.error("Invalid URL '{}' defined in properties", urlProperty, e)
        }

        settings.youTrackConnectionSettings.username = properties.getProperty(YOUTRACK_USERNAME_PROPERTY)

        val encryptedPermanentToken = properties.getProperty(YOUTRACK_PERMANENT_TOKEN)
        encryptedPermanentToken?.takeIf { it.isNotBlank() }?.let {
            try {
                settings.youTrackConnectionSettings.permanentToken = EncryptionUtil.decryptEncryptedString(encryptedPermanentToken)
            } catch (e: GeneralSecurityException) {
                LOGGER.error("Could not decrypt permanent token from settings file", e)
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.permanenttoken.decrypt", e)
            }
        }
    }

    private fun applyWindowSettings(settings: Settings, properties: Properties) {
        val windowXAsString = properties.getProperty(WINDOW_X_PROPERTY)
        windowXAsString?.takeIf { it.isNotBlank() }?.let {
            try {
                settings.windowSettings.positionX = parseInt(windowXAsString)
            } catch (e: NumberFormatException) {
                // ignore
                LOGGER.warn("Could not convert $windowXAsString to Integer for setting $WINDOW_X_PROPERTY")
            }
        }

        val windowYAsString = properties.getProperty(WINDOW_Y_PROPERTY)
        windowYAsString?.takeIf { it.isNotBlank() }?.let {
            try {
                settings.windowSettings.positionY = parseInt(windowYAsString)
            } catch (e: NumberFormatException) {
                // ignore
                LOGGER.warn("Could not convert $windowYAsString to Integer for setting $WINDOW_Y_PROPERTY")
            }
        }

        val windowWidthAsString = properties.getProperty(WINDOW_WIDTH_PROPERTY)
        windowWidthAsString?.takeIf { it.isNotBlank() }?.let {
            try {
                settings.windowSettings.width = parseInt(windowWidthAsString)
            } catch (e: NumberFormatException) {
                // ignore
                LOGGER.warn("Could not convert $windowWidthAsString to Integer for setting $WINDOW_WIDTH_PROPERTY")
            }
        }

        val windowHeightAsString = properties.getProperty(WINDOW_HEIGHT_PROPERTY)
        windowHeightAsString?.takeIf { it.isNotBlank() }?.let {
            try {
                settings.windowSettings.height = parseInt(windowHeightAsString)
            } catch (e: NumberFormatException) {
                // ignore
                LOGGER.warn("Could not convert $windowHeightAsString to Integer for setting $WINDOW_HEIGHT_PROPERTY")
            }
        }
    }

    private fun applyGeneralSettings(settings: Settings, properties: Properties) {
        val workHoursAsString = properties.getProperty(WORK_HOURS_PROPERTY)

        if (workHoursAsString.isNullOrBlank().not()) {
            try {
                settings.workHoursADay = parseFloat(workHoursAsString)
            } catch (e: NumberFormatException) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", workHoursAsString, WORK_HOURS_PROPERTY)
            }
        }

        val showOnlyOwnWorklogsAsString = properties.getProperty(SHOW_ALL_WORKLOGS_PROPERTY)
        if (showOnlyOwnWorklogsAsString.isNullOrBlank().not()) {
            settings.isShowAllWorklogs = java.lang.Boolean.valueOf(showOnlyOwnWorklogsAsString)
        }

        val showStatisticsAsString = properties.getProperty(SHOW_STATISTICS_PROPERTY)
        if (showStatisticsAsString.isNullOrBlank().not()) {
            settings.isShowStatistics = java.lang.Boolean.valueOf(showStatisticsAsString)
        }

        val autoloadDataAsString = properties.getProperty(AUTOLOAD_DATA_PROPERTY)
        if (autoloadDataAsString.isNullOrBlank().not()) {
            settings.isLoadDataAtStartup = java.lang.Boolean.valueOf(autoloadDataAsString)
        }

        val showDecimalHoursInExcel = properties.getProperty(SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT)
        if (showDecimalHoursInExcel.isNullOrBlank().not()) {
            settings.isShowDecimalHourTimesInExcelReport = java.lang.Boolean.valueOf(showDecimalHoursInExcel)
        }

        val autoloadDataTimerangeAsString = properties.getProperty(AUTOLOAD_DATA_TIMERANGE_PROPERTY)
        if (autoloadDataTimerangeAsString.isNullOrBlank().not()) {
            try {
                val timerangeProvider = TimerangeProviders.fromKey(autoloadDataTimerangeAsString)
                if (timerangeProvider != null) {
                    settings.lastUsedReportTimerange = timerangeProvider
                }
            } catch (e: IllegalArgumentException) {
                LOGGER.warn("Could not determine ReportTimerange by settings value {}", autoloadDataTimerangeAsString)
            }

        }

        val collapseStateAsString = properties.getProperty(COLLAPSE_STATE_PROPERTY)
        if (collapseStateAsString.isNullOrBlank().not()) {
            try {
                settings.collapseState.set(getDayOfWeeksFromBitmask(parseInt(collapseStateAsString)))
            } catch (e: NumberFormatException) {
                LOGGER.warn("Could not get collapse state from {}", collapseStateAsString)
            }

        }

        val highlightStateAsString = properties.getProperty(HIGHLIGHT_STATE_PROPERTY)
        if (highlightStateAsString.isNullOrBlank().not()) {
            try {
                settings.highlightState.set(getDayOfWeeksFromBitmask(parseInt(highlightStateAsString)))
            } catch (e: NumberFormatException) {
                LOGGER.warn("Could not get highlight state from {}", highlightStateAsString)
            }

        }
    }

    private fun getDayOfWeeksFromBitmask(bitmask: Int): Set<DayOfWeek> {
        return DayOfWeek.values().asSequence()
            .filter { isBitSet(it, bitmask) }
            .toSet()
    }

    private fun isBitSet(value: DayOfWeek, bitmask: Int): Boolean {
        val bitValue = 1 shl value.ordinal
        return bitmask and bitValue == bitValue
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(PropertiesSettingsLoader::class.java)

        private const val WINDOW_X_PROPERTY = "window.x"
        private const val WINDOW_Y_PROPERTY = "window.y"
        private const val WINDOW_WIDTH_PROPERTY = "window.width"
        private const val WINDOW_HEIGHT_PROPERTY = "window.height"
        private const val WORK_HOURS_PROPERTY = "workhours"
        private const val YOUTRACK_USERNAME_PROPERTY = "username"
        private const val YOUTRACK_URL_PROPERTY = "youtrackurl"
        private const val YOUTRACK_PERMANENT_TOKEN = "permanent_token"
        private const val SHOW_ALL_WORKLOGS_PROPERTY = "showonlyowntimelogs.enabled"
        private const val SHOW_STATISTICS_PROPERTY = "statistics.enabled"
        private const val AUTOLOAD_DATA_PROPERTY = "autoload.enabled"
        private const val AUTOLOAD_DATA_TIMERANGE_PROPERTY = "autoload.timerange"
        private const val SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT = "excel.decimaltimes"
        private const val COLLAPSE_STATE_PROPERTY = "collapse.state"
        private const val HIGHLIGHT_STATE_PROPERTY = "highlight.state"
    }

}
