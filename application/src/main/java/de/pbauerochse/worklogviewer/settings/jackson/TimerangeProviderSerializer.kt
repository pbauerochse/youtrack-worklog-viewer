package de.pbauerochse.worklogviewer.settings.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import de.pbauerochse.worklogviewer.timerange.TimerangeProvider

class TimerangeProviderSerializer : StdSerializer<TimerangeProvider>(TimerangeProvider::class.java) {
    override fun serialize(value: TimerangeProvider, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.settingsKey)
    }
}
