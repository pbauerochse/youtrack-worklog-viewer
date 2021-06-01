package de.pbauerochse.worklogviewer.connector.v2019.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-WorkItemType.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackWorkItemType @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name : String?
)
