package de.pbauerochse.worklogviewer.settings.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import de.pbauerochse.worklogviewer.timerange.TimerangeProvider
import de.pbauerochse.worklogviewer.timerange.TimerangeProviders

class TimerangeProviderDeserializer : StdDeserializer<TimerangeProvider>(TimerangeProvider::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): TimerangeProvider? {
        val timerangeProviderKey = p.text
        return TimerangeProviders.fromKey(timerangeProviderKey)
    }
}