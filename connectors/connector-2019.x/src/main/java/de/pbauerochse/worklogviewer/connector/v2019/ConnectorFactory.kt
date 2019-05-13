package de.pbauerochse.worklogviewer.connector.v2019

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorFactory
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.version.Version

class ConnectorFactory : YouTrackConnectorFactory {

    override val supportedVersions: List<YouTrackVersion> = listOf(
        YouTrackVersion("v2019.x", "2019.x", Version.fromVersionString("2019.1.0"))
    )
    override fun createServiceInstance(settings: YouTrackConnectionSettings): YouTrackConnector = Connector(settings)

}