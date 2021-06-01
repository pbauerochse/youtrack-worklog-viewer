package de.pbauerochse.worklogviewer.datasource.api

import de.pbauerochse.worklogviewer.datasource.ConnectionSettings
import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource
import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSourceFactory

/**
 * [TimeTrackingDataSourceFactory] for the default YouTrack REST API. Creates an instance of
 * [RestApiDataSource]
 */
class RestApiDataSourceFactory : TimeTrackingDataSourceFactory {
    override val id: String = CONNECTOR_ID
    override val name: String = RestApiDataSource.I18N.get("connector.rest.name")
    override fun createDataSourceInstance(settings: ConnectionSettings): TimeTrackingDataSource = RestApiDataSource(settings)

    companion object {
        const val CONNECTOR_ID = "v2019.x"
    }
}