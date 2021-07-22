package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-User.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class User @JsonCreator constructor(
    @JsonProperty("id") val id : String,
    @JsonProperty("login") val login : String,
    @JsonProperty("fullName") val fullName : String,
    @JsonProperty("email") val email : String?
)
