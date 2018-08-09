package de.pbauerochse.worklogviewer.version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by patrick on 01.11.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubVersion {

    @JsonProperty("html_url")
    private String url;

    @JsonProperty("tag_name")
    private String version;

    @JsonProperty("draft")
    private boolean draft;

    @JsonProperty("published_at")
    private Date published;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }
}
