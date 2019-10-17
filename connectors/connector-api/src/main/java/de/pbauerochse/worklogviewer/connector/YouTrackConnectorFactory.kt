package de.pbauerochse.worklogviewer.connector

/**
 * Allows creating a [YouTrackConnector]
 * that can connect to a YouTrack instance
 * of any of the supported versions
 */
interface YouTrackConnectorFactory {

    /**
     * Returns a list of supported
     * YouTrackVersions, this connector
     * can handle
     */
    val supportedVersions : List<YouTrackVersion>

    /**
     * Returns the instance of the [YouTrackConnector]
     * that can create and handle the TimeReport
     */
    fun createServiceInstance(settings: YouTrackConnectionSettings) : YouTrackConnector
}