package de.pbauerochse.worklogviewer.connector.v2018.domain.issue

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

@JsonIgnoreProperties(ignoreUnknown = true)
data class IssueField @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("value") val value: JsonNode
) {

    val textValue: String? by lazy {
        toGroupByKey(value)
    }

    private fun toGroupByKey(node: JsonNode): String? = when {
        node.isTextual -> node.textValue()
        node.isArray -> toGroupByKey(node as ArrayNode)
        node.isObject -> toGroupByKey(node as ObjectNode)
        else -> throw IllegalStateException("Unknown node type ${value.nodeType} for value $value")
    }

    private fun toGroupByKey(node: ArrayNode): String? = node
        .map { toGroupByKey(it) }
        .filter { it?.isNotBlank() ?: false }
        .joinToString(",")

    private fun toGroupByKey(node: ObjectNode): String? {
        return when {
            node.has("value") -> toGroupByKey(node.get("value"))
            else -> null
        }
    }

}