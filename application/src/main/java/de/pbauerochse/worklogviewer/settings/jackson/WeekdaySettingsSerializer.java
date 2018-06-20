package de.pbauerochse.worklogviewer.settings.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.pbauerochse.worklogviewer.settings.WeekdaySettings;

import java.io.IOException;
import java.time.DayOfWeek;

/**
 * Writes the Weekday Settings Bit Mask as bit
 */
public class WeekdaySettingsSerializer extends StdSerializer<WeekdaySettings> {

    protected WeekdaySettingsSerializer() {
        super(WeekdaySettings.class);
    }

    @Override
    public void serialize(WeekdaySettings value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();

        for (DayOfWeek dayOfWeek : value.getAll()) {
            gen.writeString(dayOfWeek.name());
        }

        gen.writeEndArray();
    }
}
