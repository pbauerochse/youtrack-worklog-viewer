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

    // TODO temporary field, can be removed once https://youtrack.jetbrains.com/issue/JT-47943 is released
    val workdateFieldName: String

}