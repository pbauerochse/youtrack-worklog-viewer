package de.pbauerochse.worklogviewer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class JacksonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String writeObject(Object object) throws JsonProcessingException {
        return objectMapper.writer().writeValueAsString(object);
    }

    public static <T> T parseValue(Reader contentReader, Class<T> tClass) throws IOException {
        return objectMapper.readValue(contentReader, tClass);
    }

    public static <T> T parseValue(Reader contentReader, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(contentReader, typeReference);
    }
}
