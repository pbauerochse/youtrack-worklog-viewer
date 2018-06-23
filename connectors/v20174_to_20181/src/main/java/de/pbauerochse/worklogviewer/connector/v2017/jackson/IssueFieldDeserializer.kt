package de.pbauerochse.worklogviewer.connector.v2017.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import de.pbauerochse.worklogviewer.connector.v2017.domain.issuedetails.IssueField
import org.slf4j.LoggerFactory

class IssueFieldDeserializer : StdDeserializer<IssueField>(IssueField::class.java) {

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): IssueField {
        val node = parser.codec.readTree<JsonNode>(parser)
        val valueNode = node.get("value")

        val name = node.get("name").asText()
        val value = if (valueNode.isTextual) valueNode.asText() else "0"

        if (valueNode.isTextual.not()) {
            LOGGER.info("Value JsonNode for field '$name' is not of type 'textual' but of type '${valueNode.nodeType}'")
        }

        return IssueField(name, value)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueFieldDeserializer::class.java)
    }
}