package de.pbauerochse.worklogviewer.connector.v2019.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackProject @JsonCreator constructor(
    @JsonProperty("name") val name : String?,
    @JsonProperty("shortName") val shortName : String?
)
