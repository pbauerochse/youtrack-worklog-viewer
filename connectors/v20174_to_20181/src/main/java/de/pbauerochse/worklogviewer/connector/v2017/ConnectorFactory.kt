package de.pbauerochse.worklogviewer.connector.v2017

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorFactory
import de.pbauerochse.worklogviewer.connector.YouTrackVersion

/**
 * [YouTrackConnectorFactory] for a connector
 * that can handle version 2017.4 to 2018.1
 * of the YouTrack API
 */
class ConnectorFactory : YouTrackConnectorFactory {

    override val supportedVersions: List<YouTrackVersion> = listOf(
        YouTrackVersion("2017.4 - 2018.1")
    )

    override fun createServiceInstance(settings: YouTrackConnectionSettings): YouTrackConnector {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}