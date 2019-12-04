package de.pbauerochse.worklogviewer.connector.v2019.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory

@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackCustomField @JsonCreator constructor(
    @JsonProperty("name") val name: String?,
    @JsonProperty("value") val rawValue: JsonNode?
) {

    val values: List<YouTrackCustomFieldValue>
        @JsonIgnore get() = rawValue?.let { jsonNode ->
            return when {
                jsonNode.isArray -> readValues(jsonNode as ArrayNode)
                jsonNode.isNull -> emptyList()
                jsonNode.isObject -> listOf(readNodeValue(jsonNode))
                jsonNode.isNumber || jsonNode.isTextual || jsonNode.isBoolean -> listOf(YouTrackCustomFieldValue(jsonNode.textValue()))
                else -> {
                    LOGGER.warn("Unhandled YouTrackCustomField Value: ${jsonNode.nodeType}: $jsonNode. Defaulting to emptyList")
                    emptyList()
                }
            }
        } ?: emptyList()

    private fun readValues(arrayNode: ArrayNode): List<YouTrackCustomFieldValue> {
        return arrayNode.map { readNodeValue(it) }
    }

    private fun readNodeValue(node: JsonNode): YouTrackCustomFieldValue {
        val stringValue: String? = node.toString()
        return jacksonObjectMapper().readValue(stringValue, YouTrackCustomFieldValue::class.java)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(YouTrackCustomField::class.java)
    }
}
