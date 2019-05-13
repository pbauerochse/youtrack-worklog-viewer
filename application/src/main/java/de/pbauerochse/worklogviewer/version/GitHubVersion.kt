package de.pbauerochse.worklogviewer.version

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
class GitHubVersion @JsonCreator constructor(
    @JsonProperty("html_url") val url: String,
    @JsonProperty("tag_name") val version: String,
    @JsonProperty("draft") val isDraft: Boolean,
    @JsonProperty("published_at") val published: Date
) {

    val isRelease: Boolean
        @JsonIgnore get() = !isDraft

}
