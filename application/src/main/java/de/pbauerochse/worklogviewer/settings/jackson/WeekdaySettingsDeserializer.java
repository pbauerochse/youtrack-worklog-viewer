package de.pbauerochse.worklogviewer.settings.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.pbauerochse.worklogviewer.settings.WeekdaySettings;

import java.io.IOException;
import java.time.DayOfWeek;

/**
 * Writes the Weekday Settings Bit Mask as bit
 */
public class WeekdaySettingsDeserializer extends StdDeserializer<WeekdaySettings> {

    protected WeekdaySettingsDeserializer() {
        super(WeekdaySettings.class);
    }

    @Override
    public WeekdaySettings deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return null;
        }

        WeekdaySettings weekdaySettings = new WeekdaySettings();

        JsonToken jsonToken;
        while ((jsonToken = p.nextToken()) != null && !jsonToken.isStructEnd()) {
            String dayOfWeekName = p.getText();
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekName);

            weekdaySettings.set(dayOfWeek, true);
        }

        return weekdaySettings;
    }
}
