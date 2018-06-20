package de.pbauerochse.worklogviewer.settings.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import de.pbauerochse.worklogviewer.toLocalDate

import java.io.IOException
import java.time.LocalDate

/**
 * JSON Deserializer for LocalDates
 */
class LocalDateDeserializer : StdDeserializer<LocalDate?>(LocalDate::class.java) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): LocalDate? {
        val formattedDate = jsonParser.text
        return formattedDate.toLocalDate()
    }

}
