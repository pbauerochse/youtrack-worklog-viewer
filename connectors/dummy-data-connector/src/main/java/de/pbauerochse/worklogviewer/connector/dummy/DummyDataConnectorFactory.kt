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
    override fun createServiceInstance(settings: YouTrackConnectionSettings): YouTrackConnector = DummyDataConnector(settings.username)
    override val supportedVersions: List<YouTrackVersion> = listOf(
        YouTrackVersion("DUMMY", "Generated Example Data", Version.fromVersionString("0.0.0"))
    )
}