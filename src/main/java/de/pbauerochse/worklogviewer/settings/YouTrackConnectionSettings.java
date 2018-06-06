package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;

/**
 * Contains the settings required to use the
 * YouTrack API
 */
public interface YouTrackConnectionSettings {

    YouTrackVersion getVersion();

    YouTrackAuthenticationMethod getAuthenticationMethod();

    @Deprecated
    String getOAuthHubUrl();

    @Deprecated
    String getOAuthServiceId();

    @Deprecated
    String getOAuthServiceSecret();

    String getUsername();

    @Deprecated
    String getPassword();

    String getUrl();

    String getPermanentToken();

}
