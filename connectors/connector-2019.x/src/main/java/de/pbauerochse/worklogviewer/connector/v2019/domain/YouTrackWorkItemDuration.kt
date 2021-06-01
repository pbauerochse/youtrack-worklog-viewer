package de.pbauerochse.worklogviewer.connector.v2019.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-DurationValue.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class YouTrackWorkItemDuration @JsonCreator constructor(
    @JsonProperty("minutes") val minutes : Long,
    @JsonProperty("presentation") val presentation : String
)
