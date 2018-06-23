package de.pbauerochse.worklogviewer.settings.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator
import de.pbauerochse.worklogviewer.connector.YouTrackVersion

class YouTrackVersionDeserializer : StdDeserializer<YouTrackVersion>(YouTrackVersion::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): YouTrackVersion? {
        val versionId = p.text
        return YouTrackConnectorLocator.getSupportedVersions()
            .find { it.id == versionId }
    }
}