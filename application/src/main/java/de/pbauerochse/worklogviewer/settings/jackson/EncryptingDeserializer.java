package de.pbauerochse.worklogviewer.settings.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.pbauerochse.worklogviewer.util.EncryptionUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class EncryptingDeserializer extends StdDeserializer<String> {

    public EncryptingDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String encrytpedText = p.getText();
        try {
            return EncryptionUtil.decryptEncryptedString(encrytpedText);
        } catch (GeneralSecurityException e) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.password.decrypt", e);
        }
    }
}
