package de.pbauerochse.worklogviewer.settings.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.pbauerochse.worklogviewer.util.EncryptionUtil;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;

public class EncryptingDeserializer extends StdDeserializer<String> {

    public EncryptingDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String encrytpedText = p.getText();
        try {
            return EncryptionUtil.decryptEncryptedString(encrytpedText);
        } catch (GeneralSecurityException e) {
            LoggerFactory.getLogger(EncryptingDeserializer.class).warn(getFormatted("exceptions.settings.password.decrypt", e));
            return null;
        }
    }
}
