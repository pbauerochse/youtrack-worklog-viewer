package de.pbauerochse.worklogviewer.connector

import java.net.URL

/**
 * Contains all parameters required to
 * authenticate with a YouTrack instance
 */
interface YouTrackConnectionSettings {
    val version : YouTrackVersion
    val baseUrl : URL
    val permanentToken : String
}