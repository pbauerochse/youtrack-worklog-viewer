package de.pbauerochse.worklogviewer.connector.v2018

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorFactory
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.http.Http
import org.slf4j.LoggerFactory

/**
 * [YouTrackConnectorFactory] for a YouTrack 2018.2
 * connector
 */
class ConnectorFactory : YouTrackConnectorFactory {

    override val supportedVersions: List<YouTrackVersion> = listOf(SupportedVersions.v2018_2)

    override fun createServiceInstance(settings: YouTrackConnectionSettings): YouTrackConnector {
        LOGGER.debug("Creating new connector for $settings")
        val http = Http(settings)
        return Connector(http)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ConnectorFactory::class.java)
    }
}