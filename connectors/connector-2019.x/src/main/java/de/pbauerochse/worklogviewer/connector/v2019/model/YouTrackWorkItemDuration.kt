package de.pbauerochse.worklogviewer.connector.v2019.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackWorkItemDuration @JsonCreator constructor(
    @JsonProperty("minutes") val minutes : Long,
    @JsonProperty("presentation") val presentation : String
)
