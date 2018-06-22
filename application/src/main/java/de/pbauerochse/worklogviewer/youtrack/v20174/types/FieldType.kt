package de.pbauerochse.worklogviewer.youtrack.v20174.types

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@Deprecated("")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class FieldType @JsonCreator
constructor(@param:JsonProperty("id") val id: String) {

    val type: String
        @JsonProperty("\$type")
        get() = "jetbrains.charisma.persistence.customfields.FieldType"
}
