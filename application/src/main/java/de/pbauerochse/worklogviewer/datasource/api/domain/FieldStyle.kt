package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-FieldStyle.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class FieldStyle @JsonCreator constructor(
    @JsonProperty("background") val backgroundColor: String?,
    @JsonProperty("foreground") val foregroundColor: String?
)
