package de.pbauerochse.worklogviewer.youtrack.connector;

import com.intellij.hub.auth.oauth2.token.AccessToken;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import jetbrains.jetpass.client.hub.HubClient;
import jetbrains.jetpass.client.oauth2.OAuth2Client;
import jetbrains.jetpass.client.oauth2.token.OAuth2ResourceOwnerFlow;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 14.10.15
 */
class OAuth2Connector extends ApiLoginConnector implements YouTrackConnector {

    public static final String YOUTRACK_SCOPE = "YouTrack";

    private AccessToken accessToken;

    @Override
    protected void addDefaultHeaders(List<Header> headerList) {
        super.addDefaultHeaders(headerList);

        if (accessToken == null || new Date().after(accessToken.getExpirationDate())) {
            LOGGER.debug("Fetching new AccessToken");
            SettingsUtil.Settings settings = SettingsUtil.loadSettings();

            try {
                URL hubUrl = getHubUrl(settings.getYoutrackUrl());

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
            } catch (MalformedURLException e) {
                LOGGER.error("Malformed URL", e);
            }
        }

        if (accessToken != null) {
            LOGGER.debug("Setting AccessToken");
            headerList.add(new BasicHeader("Authorization", String.format("%s %s", accessToken.getType(), accessToken.encode())));
        }
    }

    private URL getHubUrl(String baseUrl) throws MalformedURLException {
        if (StringUtils.isBlank(baseUrl)) {
            throw ExceptionUtil.getIllegalArgumentException("exceptions.main.worker.youtrackurl");
        }

        StringBuilder sb = new StringBuilder(baseUrl);
        while (sb.charAt(sb.length() - 1) == '/') {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append("/hub");

        return new URL(sb.toString());
    }

    @Override
    public YouTrackAuthenticationMethod getAuthenticationMethod() {
        return YouTrackAuthenticationMethod.OAUTH2;
    }
}
