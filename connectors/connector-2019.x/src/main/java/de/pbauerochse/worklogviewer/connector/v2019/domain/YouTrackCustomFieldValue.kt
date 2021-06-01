package de.pbauerochse.worklogviewer.connector.v2019.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackCustomFieldValue @JsonCreator constructor(
    @JsonProperty("name") val value : String?
)