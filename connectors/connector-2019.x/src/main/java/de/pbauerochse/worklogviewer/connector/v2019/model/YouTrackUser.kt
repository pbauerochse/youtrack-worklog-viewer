package de.pbauerochse.worklogviewer.connector.v2019.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackUser @JsonCreator constructor(
    @JsonProperty("login") val login : String?,
    @JsonProperty("fullName") val fullName : String?,
    @JsonProperty("email") val email : String?
)
