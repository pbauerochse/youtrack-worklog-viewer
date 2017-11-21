package de.pbauerochse.worklogviewer.youtrack.authentication;

import com.google.common.collect.ImmutableList;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationProvider;
import de.pbauerochse.worklogviewer.youtrack.YouTrackUrlBuilder;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import java.util.List;

/**
 * Connector which uses permanent tokens to
 * authenticate with YouTrack
 * <p>
 * The users needs to have "Read Service" role to
 * create a permanent token.
 */
public class BearerTokenAuthenticationProvider implements YouTrackAuthenticationProvider {

    @Override
    public List<Header> getAuthenticationHeaders(HttpClientBuilder clientBuilder, YouTrackUrlBuilder urlBuilder) {
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();
        return ImmutableList.of(
                new BasicHeader("Authorization", "Bearer " + settings.getYoutrackPermanentToken())
        );
    }

    @Override
    public YouTrackAuthenticationMethod getMethod() {
        return YouTrackAuthenticationMethod.PERMANENT_TOKEN;
    }

}
