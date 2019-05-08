package de.pbauerochse.worklogviewer.connector

import java.net.URL

/**
 * Contains all parameters required to
 * authenticate with a YouTrack instance
 */
interface YouTrackConnectionSettings {
    var version : YouTrackVersion?
    var baseUrl : URL?
    var permanentToken : String?
}