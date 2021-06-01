package de.pbauerochse.worklogviewer.datasource

import java.net.URL

/**
 * Contains all parameters required to
 * authenticate with a YouTrack instance
 */
interface ConnectionSettings {
    var selectedConnectorId: String?
    var baseUrl: URL?
    var permanentToken: String?
    var username: String?
}