package de.pbauerochse.worklogviewer.youtrack.authentication

import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationProvider
import de.pbauerochse.worklogviewer.youtrack.YouTrackUrlBuilder
import org.apache.http.Header
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicHeader

/**
 * Connector which uses permanent tokens to
 * authenticate with YouTrack
 *
 *
 * The users needs to have "Read Service" role to
 * create a permanent token.
 */
@Deprecated("")
class BearerTokenAuthenticationProvider : YouTrackAuthenticationProvider {

    override fun getAuthenticationHeaders(clientBuilder: HttpClientBuilder, urlBuilder: YouTrackUrlBuilder): List<Header> {
        val settings = SettingsUtil.settings
        return listOf(
            BasicHeader("Authorization", "Bearer " + settings.youTrackConnectionSettings.permanentToken)
        )
    }

}
