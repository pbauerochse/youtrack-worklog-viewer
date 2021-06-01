package de.pbauerochse.worklogviewer.connector.v2019.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-Project.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackProject @JsonCreator constructor(
    @JsonProperty("id") val id : String,
    @JsonProperty("name") val name : String?,
    @JsonProperty("shortName") val shortName : String?
)
