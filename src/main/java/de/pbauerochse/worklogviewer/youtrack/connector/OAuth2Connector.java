package de.pbauerochse.worklogviewer.youtrack.connector;

import com.intellij.hub.auth.oauth2.token.AccessToken;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import jetbrains.jetpass.client.hub.HubClient;
import jetbrains.jetpass.client.oauth2.OAuth2Client;
import jetbrains.jetpass.client.oauth2.token.OAuth2ResourceOwnerFlow;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 14.10.15
 */
public class OAuth2Connector extends YouTrackConnectorBase {

    private static final String YOUTRACK_SCOPE = "YouTrack";

    private Integer connectionParameterHashCode;

    private AccessToken accessToken;

    private CloseableHttpClient client;

    @Override
    protected CloseableHttpClient performLoginIfNecessary(HttpClientBuilder clientBuilder, List<Header> requestHeaders) throws Exception {

        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        if (client == null || accessToken == null || new Date().after(accessToken.getExpirationDate()) || (connectionParameterHashCode != null && !connectionParameterHashCode.equals(settings.getConnectionParametersHashCode()))) {
            LOGGER.info("Fetching new access token");

            try {
                URL hubUrl = new URL(settings.getYoutrackOAuthHubUrl());

                HubClient hubClient = HubClient.builder().baseUrl(hubUrl).build();

                OAuth2Client oAuthClient = hubClient.getOAuthClient();
                OAuth2ResourceOwnerFlow flow = oAuthClient.resourceOwnerFlow()
                        .clientId(settings.getYoutrackOAuthServiceId())
                        .clientSecret(settings.getYoutrackOAuthServiceSecret())
                        .username(settings.getYoutrackUsername())
                        .password(settings.getYoutrackPassword())
                        .addScope(YOUTRACK_SCOPE)
                        .build();

                accessToken = flow.getToken();
                if (accessToken == null) {
                    throw ExceptionUtil.getIllegalStateException("exceptions.oauth.couldnotobtainaccesstoken");
                }

                LOGGER.debug("Setting AccessToken");

                String requestHeaderValue = String.format("%s %s", accessToken.getType(), accessToken.encode());
                requestHeaders.add(new BasicHeader("Authorization", requestHeaderValue));

                // everythings fine
                // set headers and initialize client
                connectionParameterHashCode = settings.getConnectionParametersHashCode();
                client = clientBuilder
                        .setDefaultHeaders(requestHeaders)
                        .build();

            } catch (MalformedURLException e) {
                LOGGER.error("Malformed URL", e);
                throw ExceptionUtil.getIllegalArgumentException("exceptions.main.oauth2.huburl", e);
            }
        }

        return client;
    }

    @Override
    public YouTrackAuthenticationMethod getAuthenticationMethod() {
        return YouTrackAuthenticationMethod.OAUTH2;
    }
}
