package de.pbauerochse.worklogviewer.settings.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import de.pbauerochse.worklogviewer.connector.YouTrackVersion

class YouTrackVersionSerializer : StdSerializer<YouTrackVersion>(YouTrackVersion::class.java) {

    override fun serialize(value: YouTrackVersion, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.id)
    }
}