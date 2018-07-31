package de.pbauerochse.worklogviewer.connector.v2017

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorFactory
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.connector.v2017.url.UrlBuilderFactory
import de.pbauerochse.worklogviewer.http.Http
import org.slf4j.LoggerFactory

/**
 * [YouTrackConnectorFactory] for a connector
 * that can handle version 2017.4 to 2018.1
 * of the YouTrack API
 */
open class ConnectorFactory : YouTrackConnectorFactory {

    override val supportedVersions: List<YouTrackVersion> = listOf(
        SupportedVersions.v2017_4, SupportedVersions.v2018_1
    )

    override fun createServiceInstance(settings: YouTrackConnectionSettings): YouTrackConnector {
        LOGGER.debug("Creating new connector for $settings")
        val urlBuilder = UrlBuilderFactory.getUrlBuilder(settings)
        val http = Http(settings)
        return Connector(urlBuilder, http)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ConnectorFactory::class.java)
    }
}