package de.pbauerochse.worklogviewer.youtrack

import de.pbauerochse.worklogviewer.settings.Settings
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.ExceptionUtil.getIllegalArgumentException
import de.pbauerochse.worklogviewer.youtrack.v20174.UrlBuilder
import de.pbauerochse.worklogviewer.youtrack.v20174.YouTrackServiceV20174
import org.slf4j.LoggerFactory

/**
 * Factory to get the YouTrackConnector
 * configured in the settings properties
 */
object YouTrackServiceFactory {

    private val LOGGER = LoggerFactory.getLogger(YouTrackServiceFactory::class.java)
    private val AVAILABLE_SERVICE_IMPLEMENTATIONS = setOf(
        YouTrackServiceV20174(UrlBuilder(
            { SettingsUtil.settings.youTrackConnectionSettings.url },
            { SettingsUtil.settings.youTrackConnectionSettings.version }
        ))
    )

    private var cachedInstance: YouTrackService? = null

    @JvmStatic
    fun getYouTrackService(): YouTrackService {
        val settings = SettingsUtil.settings

        if (cachedInstance == null || authenticationMethodChanged(settings)) {
            cachedInstance = getYouTrackService(settings.youTrackConnectionSettings.version)
            LOGGER.info("Created new YouTrackConnector instance of type {}", cachedInstance!!.javaClass.simpleName)
        }

        return cachedInstance!!
    }

    private fun getYouTrackService(version: YouTrackVersion): YouTrackService {
        return AVAILABLE_SERVICE_IMPLEMENTATIONS.stream()
            .filter { service -> service.supportedVersions.contains(version) }
            .findFirst()
            .orElseThrow({ getIllegalArgumentException("exceptions.settings.version.invalid", version.name) })
    }

    private fun authenticationMethodChanged(settings: Settings): Boolean {
        return !cachedInstance!!.supportedVersions.contains(settings.youTrackConnectionSettings.version)
    }

}
