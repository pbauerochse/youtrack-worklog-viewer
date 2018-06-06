package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;

public class YouTrackConnectionSettingsImpl implements YouTrackConnectionSettings {

    private YouTrackVersion version;
    private YouTrackAuthenticationMethod authenticationMethod;

    private String url;
    private String OAuthHubUrl;
    private String OAuthServiceId;
    private String OAuthServiceSecret;

    private String permanentToken;

    private String username;
    private String password;

    @Override
    public YouTrackVersion getVersion() {
        return version;
    }

    @Override
    public YouTrackAuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getOAuthHubUrl() {
        return OAuthHubUrl;
    }

    @Override
    public String getOAuthServiceId() {
        return OAuthServiceId;
    }

    @Override
    public String getOAuthServiceSecret() {
        return OAuthServiceSecret;
    }

    @Override
    public String getPermanentToken() {
        return permanentToken;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
