package de.pbauerochse.worklogviewer.connector.dummy

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorFactory
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.version.Version

/**
 * Connector factory that provides dummy data for testing
 */
class DummyDataConnectorFactory : YouTrackConnectorFactory {
    override fun createServiceInstance(settings: YouTrackConnectionSettings): YouTrackConnector = DummyDataConnector()
    override val supportedVersions: List<YouTrackVersion> = listOf(
        YouTrackVersion("v2019.x_DUMMY", "2019.x (dummy data)", Version.fromVersionString("2019.1.0"))
    )
}