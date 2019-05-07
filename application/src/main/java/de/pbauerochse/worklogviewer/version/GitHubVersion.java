package de.pbauerochse.worklogviewer.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by patrick on 01.11.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubVersion {

    private final String url;
    private final String version;
    private final boolean draft;
    private final Date published;

    @JsonCreator
    public GitHubVersion(@JsonProperty("html_url") String url,
                         @JsonProperty("tag_name") String version,
                         @JsonProperty("draft") boolean draft,
                         @JsonProperty("published_at") Date published) {
        this.url = url;
        this.version = version;
        this.draft = draft;
        this.published = published;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public boolean isDraft() {
        return draft;
    }

    public boolean isRelease() {
        return !isDraft();
    }

    public Date getPublished() {
        return published;
    }

}
