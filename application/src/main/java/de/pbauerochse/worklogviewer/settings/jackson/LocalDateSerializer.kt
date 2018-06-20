package de.pbauerochse.worklogviewer.settings.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import de.pbauerochse.worklogviewer.toFormattedString
import java.time.LocalDate

/**
 * JSON Serializer for LocalDates
 */
class LocalDateSerializer : StdSerializer<LocalDate>(LocalDate::class.java) {

    override fun serialize(value: LocalDate, generator: JsonGenerator, serializer: SerializerProvider) {
        generator.writeString(value.toFormattedString())
    }
}
