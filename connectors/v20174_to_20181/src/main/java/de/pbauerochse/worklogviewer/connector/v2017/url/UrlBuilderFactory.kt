package de.pbauerochse.worklogviewer.connector.v2017.url

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.v2017.SupportedVersions
import org.slf4j.LoggerFactory

/**
 * URLs are slightly different
 * in the supported YouTrack
 * versions, hence we must create
 * different connection endpoints
 */
object UrlBuilderFactory {

    private val LOGGER = LoggerFactory.getLogger(UrlBuilderFactory::class.java)

    @JvmStatic
    fun getUrlBuilder(settings : YouTrackConnectionSettings) : UrlBuilder {
        LOGGER.debug("Loading UrlBuilder for ${settings.version}")
        val baseUrl = settings.baseUrl
        return when (settings.version) {
            SupportedVersions.v2017_4 -> V2017UrlBuilder(baseUrl)
            SupportedVersions.v2018_1 -> V2018UrlBuilder(baseUrl)
            else -> throw IllegalArgumentException("Unsupported YouTrack version ${settings.version}")
        }
    }

}