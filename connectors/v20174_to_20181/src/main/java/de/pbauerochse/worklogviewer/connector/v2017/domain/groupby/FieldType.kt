package de.pbauerochse.worklogviewer.connector.v2017.domain.groupby

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class FieldType @JsonCreator constructor(@JsonProperty("id") val id: String) {

    @get:JsonProperty("\$type")
    val type: String = "jetbrains.charisma.persistence.customfields.FieldType"

}