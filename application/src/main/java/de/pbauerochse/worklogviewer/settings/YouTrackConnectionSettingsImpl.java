package de.pbauerochse.worklogviewer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.pbauerochse.worklogviewer.connector.YouTrackVersion;
import de.pbauerochse.worklogviewer.settings.jackson.EncryptingDeserializer;
import de.pbauerochse.worklogviewer.settings.jackson.EncryptingSerializer;
import de.pbauerochse.worklogviewer.settings.jackson.YouTrackVersionDeserializer;
import de.pbauerochse.worklogviewer.settings.jackson.YouTrackVersionSerializer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Contains the settings required to use the
 * YouTrack API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTrackConnectionSettingsImpl implements de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings {

    private YouTrackVersion version;
    private String url;
    private String username;
    private String permanentToken;

    @JsonSerialize(using = YouTrackVersionSerializer.class)
    @JsonDeserialize(using = YouTrackVersionDeserializer.class)
    public YouTrackVersion getVersion() {
        return version;
    }
    public void setVersion(YouTrackVersion version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonSerialize(using = EncryptingSerializer.class)
    @JsonDeserialize(using = EncryptingDeserializer.class)
    public String getPermanentToken() {
        return permanentToken;
    }
    public void setPermanentToken(String permanentToken) {
        this.permanentToken = permanentToken;
    }

    @NotNull
    @Override
    public URL getBaseUrl() {
        if (StringUtils.isNotBlank(getUrl())) {
            try {
                return new URL(getUrl());
            } catch (MalformedURLException e) {
                LoggerFactory.getLogger(YouTrackConnectionSettingsImpl.class).warn("YouTrack URL '{}' does not seem to be a valid URL", url, e);
            }
        }

        throw new IllegalStateException("YouTrack Url not set yet");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("YouTrackConnectionSettings{");
        sb.append("version=").append(version);
        sb.append(", url='").append(url).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", permanentToken='").append(permanentToken).append('\'');
        sb.append(", baseUrl=").append(getBaseUrl());
        sb.append('}');
        return sb.toString();
    }
}
