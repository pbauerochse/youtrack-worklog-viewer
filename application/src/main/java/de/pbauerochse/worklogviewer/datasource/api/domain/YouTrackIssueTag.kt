package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-IssueTag.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackIssueTag @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("color") val color: YouTrackFieldStyle
)