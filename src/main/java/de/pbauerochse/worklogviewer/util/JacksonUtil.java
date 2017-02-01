package de.pbauerochse.worklogviewer.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.pbauerochse.worklogviewer.youtrack.issuedetails.IssueField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class JacksonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        SimpleModule customModule = new SimpleModule("IssueField", new Version(1, 0, 0, null, null, null))
                .addDeserializer(IssueField.class, new IssueFieldDeserializer());
        objectMapper.registerModule(customModule);
    }

    public static String writeObject(Object object) throws JsonProcessingException {
        return objectMapper.writer().writeValueAsString(object);
    }

    public static <T> T parseValue(Reader contentReader, Class<T> tClass) throws IOException {
        return objectMapper.readValue(contentReader, tClass);
    }

    public static <T> T parseValue(Reader contentReader, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(contentReader, typeReference);
    }

    private static class IssueFieldDeserializer extends JsonDeserializer<IssueField> {

        private static final Logger LOGGER = LoggerFactory.getLogger(IssueFieldDeserializer.class);

        @Override
        public IssueField deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            JsonNode valueNode = node.get("value");

            String name = node.get("name").asText();
            String value = valueNode.isTextual() ? valueNode.asText() : "0";

            if (!valueNode.isTextual()) {
                LOGGER.info("Value JsonNode for field {} is not of type textual but of type {}", name, valueNode.getNodeType());
            }

            IssueField issueField = new IssueField();
            issueField.setName(name);
            issueField.setValue(value);
            return issueField;
        }
    }
}
