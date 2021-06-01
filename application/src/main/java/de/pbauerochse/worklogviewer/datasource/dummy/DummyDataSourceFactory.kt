package de.pbauerochse.worklogviewer.datasource.dummy

import de.pbauerochse.worklogviewer.datasource.ConnectionSettings
import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource
import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSourceFactory
import de.pbauerochse.worklogviewer.i18n.I18n

/**
 * Connector factory that provides dummy data for testing
 */
class DummyDataSourceFactory : TimeTrackingDataSourceFactory {
    override val id: String = CONNECTOR_ID
    override val name: String = I18n("i18n/connectors/dummy").get("connector.dummy.name")
    override fun createDataSourceInstance(settings: ConnectionSettings): TimeTrackingDataSource = DummyDataSource(settings.username ?: "You")

    companion object {
        const val CONNECTOR_ID = "DUMMY"
    }
}