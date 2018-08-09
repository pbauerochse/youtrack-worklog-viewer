package de.pbauerochse.worklogviewer.settings.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.pbauerochse.worklogviewer.util.EncryptionUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Serializes a given string using the EncryptionUtils
 */
public class EncryptingSerializer extends StdSerializer<String> {

    public EncryptingSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        try {
            gen.writeString(EncryptionUtil.encryptCleartextString(value));
        } catch (GeneralSecurityException e) {
            throw ExceptionUtil.getIllegalStateException("exceptions.settings.password.encrypt", e);
        }
    }
}
