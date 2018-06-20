package de.pbauerochse.worklogviewer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.pbauerochse.worklogviewer.settings.jackson.EncryptingDeserializer;
import de.pbauerochse.worklogviewer.settings.jackson.EncryptingSerializer;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;

/**
 * Contains the settings required to use the
 * YouTrack API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTrackConnectionSettings {

    private YouTrackVersion version;
    private String url;
    private String username;
    private String permanentToken;

    public YouTrackVersion getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }

    @JsonSerialize(using = EncryptingSerializer.class)
    @JsonDeserialize(using = EncryptingDeserializer.class)
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
