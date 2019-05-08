package de.pbauerochse.worklogviewer.settings

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.settings.jackson.EncryptingDeserializer
import de.pbauerochse.worklogviewer.settings.jackson.EncryptingSerializer
import de.pbauerochse.worklogviewer.settings.jackson.YouTrackVersionDeserializer
import de.pbauerochse.worklogviewer.settings.jackson.YouTrackVersionSerializer
import java.net.URL

/**
 * Contains the settings required to use the
 * YouTrack API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackConnectionSettingsImpl(
    @JsonSerialize(using = YouTrackVersionSerializer::class)
    @JsonDeserialize(using = YouTrackVersionDeserializer::class)
    override var version: YouTrackVersion? = null,

    override var baseUrl: URL? = null,

    var username: String? = null,

    @JsonSerialize(using = EncryptingSerializer::class)
    @JsonDeserialize(using = EncryptingDeserializer::class)
    override var permanentToken: String? = null
) : YouTrackConnectionSettings
