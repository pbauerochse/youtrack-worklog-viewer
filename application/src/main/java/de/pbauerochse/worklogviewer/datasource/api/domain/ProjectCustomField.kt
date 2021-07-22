package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-ProjectCustomField.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectCustomField @JsonCreator constructor(
    @JsonProperty("field") val field: CustomField?,
    @JsonProperty("canBeEmpty") val canBeEmpty : Boolean,
    @JsonProperty("emptyFieldText") val emptyFieldText : String?
)
