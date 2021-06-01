package de.pbauerochse.worklogviewer.datasource

import de.pbauerochse.worklogviewer.datasource.api.RestApiDataSourceFactory
import de.pbauerochse.worklogviewer.datasource.dummy.DummyDataSourceFactory
import de.pbauerochse.worklogviewer.settings.SettingsUtil

/**
 * Provides methods to get the currently selected
 * [TimeTrackingDataSource] and all other available
 * DataSources
 */
object DataSources {

    /**
     * Hard-coded list of supported [TimeTrackingDataSourceFactory]s
     */
    val dataSourceFactories: List<TimeTrackingDataSourceFactory> = listOf(
        RestApiDataSourceFactory(), DummyDataSourceFactory()
    )

    /**
     * Returns the currently selected datasource by comparing its id
     * with the currently stored datasource id from the [ConnectionSettings]
     */
    val activeDataSource: TimeTrackingDataSource?
        get() {
            val connectionSettings = SettingsUtil.settings.youTrackConnectionSettings
            return findDataSourceFactoryById(connectionSettings.selectedConnectorId)
                ?.createDataSourceInstance(connectionSettings)
        }

    /**
     * Returns the [TimeTrackingDataSource] with the id
     * stored in the youtrackSettings
     */
    internal fun findDataSourceFactoryById(connectorId: String?): TimeTrackingDataSourceFactory? {
        return dataSourceFactories.find { it.id == connectorId }
    }
}