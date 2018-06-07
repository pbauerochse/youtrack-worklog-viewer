package de.pbauerochse.worklogviewer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;

/**
 * Contains the settings required to use the
 * YouTrack API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTrackConnectionSettings {

    private YouTrackVersion version;

    private String url;
    private String permanentToken;
    private String username;

    public YouTrackVersion getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }

    public String getPermanentToken() {
        return permanentToken;
    }

    public String getUsername() {
        return username;
    }

    public void setVersion(YouTrackVersion version) {
        this.version = version;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPermanentToken(String permanentToken) {
        this.permanentToken = permanentToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
