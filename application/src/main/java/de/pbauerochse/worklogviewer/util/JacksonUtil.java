package de.pbauerochse.worklogviewer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;

/**
 * Helper for deserialising JSON
 */
public class JacksonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T parseValue(Reader contentReader, TypeReference<T> typeReference) throws IOException {
        return OBJECT_MAPPER.readValue(contentReader, typeReference);
    }

}
