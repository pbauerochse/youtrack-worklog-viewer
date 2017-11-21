package de.pbauerochse.worklogviewer.youtrack.authentication;

import com.google.common.collect.ImmutableList;
import com.intellij.hub.auth.oauth2.token.AccessToken;
import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationProvider;
import de.pbauerochse.worklogviewer.youtrack.YouTrackUrlBuilder;
import jetbrains.jetpass.client.hub.HubClient;
import jetbrains.jetpass.client.oauth2.OAuth2Client;
import jetbrains.jetpass.client.oauth2.token.OAuth2ResourceOwnerFlow;
import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Provides OAuth2 Authentication for the YouTrack Worklog Viewer
 * <p>
 * Although I am pretty sure, I am not using the proper authentication
 * flow here. This might need reconsideration
 */
public class OAuth2AuthenticationProvider implements YouTrackAuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationProvider.class);
    private static final String YOUTRACK_SCOPE = "YouTrack";

    private Integer connectionParameterHashCode;
    private AccessToken accessToken;

    @Override
    public List<Header> getAuthenticationHeaders(HttpClientBuilder clientBuilder, YouTrackUrlBuilder urlBuilder) {
        if (isReauthenticationRequired()) {
            Settings settings = SettingsUtil.getSettings();

            accessToken = fetchAccessToken();
            connectionParameterHashCode = settings.getConnectionParametersHashCode();
        }

        String requestHeaderValue = String.format("%s %s", accessToken.getType(), accessToken.encode());
        return ImmutableList.of(
                new BasicHeader("Authorization", requestHeaderValue)
        );
    }

    @Override
    public YouTrackAuthenticationMethod getMethod() {
        return YouTrackAuthenticationMethod.OAUTH2;
    }

    private AccessToken fetchAccessToken() {
        Settings settings = SettingsUtil.getSettings();
        String hubUrl = settings.getYoutrackOAuthHubUrl();
        LOGGER.info("Fetching new access token from url {}", hubUrl);

        // TODO instead of passing the username / password here, there should actually only be the
        // access token or the refresh token

        HubClient client = HubClient.builder().baseUrl(hubUrl).build();

        OAuth2Client oAuthClient = client.getOAuthClient();
        OAuth2ResourceOwnerFlow flow = oAuthClient.resourceOwnerFlow()
                .clientId(settings.getYoutrackOAuthServiceId())
                .clientSecret(settings.getYoutrackOAuthServiceSecret())
                .username(settings.getYoutrackUsername())
                .password(settings.getYoutrackPassword())
                .addScope(YOUTRACK_SCOPE)
                .build();

        return flow.getToken();
    }

    private boolean isReauthenticationRequired() {
        return isAccessTokenInvalid() || connectionParametersHaveChanged();
    }

    private boolean isAccessTokenInvalid() {
        return accessToken == null || new Date().after(accessToken.getExpirationDate());
    }

    private boolean connectionParametersHaveChanged() {
        Settings settings = SettingsUtil.getSettings();
        return connectionParameterHashCode != null && !connectionParameterHashCode.equals(settings.getConnectionParametersHashCode());
    }

}
