package de.pbauerochse.worklogviewer.connector.v2018.domain.issue

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class IssueField @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("value") val value: JsonNode
) {

    val textValue: String? by lazy {
        if (value.isTextual) value.textValue() else null
    }

}