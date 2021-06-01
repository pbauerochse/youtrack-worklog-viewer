package de.pbauerochse.worklogviewer.datasource

/**
 * Allows creating a [TimeTrackingDataSource]
 * that can connect to a YouTrack instance
 * of any of the supported versions
 */
interface TimeTrackingDataSourceFactory {

    /**
     * A application unique Id for this connector
     */
    val id: String

    /**
     * A display name for this connector
     */
    val name: String

    /**
     * Returns the instance of the [TimeTrackingDataSource]
     * that can create and handle the TimeReport
     */
    fun createDataSourceInstance(settings: ConnectionSettings) : TimeTrackingDataSource
}