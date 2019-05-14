package de.pbauerochse.worklogviewer.connector.v2018

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorFactory
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.version.Version

class ConnectorFactory : YouTrackConnectorFactory {

    override val supportedVersions: List<YouTrackVersion> = listOf(
        YouTrackVersion("v2018.2", "2018.2", Version.fromVersionString("2018.2.0"))
    )

    override fun createServiceInstance(settings: YouTrackConnectionSettings): YouTrackConnector = Connector(settings)

}