package de.pbauerochse.worklogviewer.settings

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.pbauerochse.worklogviewer.datasource.ConnectionSettings
import de.pbauerochse.worklogviewer.settings.jackson.EncryptingDeserializer
import de.pbauerochse.worklogviewer.settings.jackson.EncryptingSerializer
import java.net.URL

/**
 * Contains the settings required to use the
 * YouTrack API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ConnectionSettingsImpl(
    override var baseUrl: URL? = null,
    override var username: String? = null,

    @JsonProperty("version") // named version for historic reasons
    override var selectedConnectorId: String? = null,

    @JsonSerialize(using = EncryptingSerializer::class)
    @JsonDeserialize(using = EncryptingDeserializer::class)
    override var permanentToken: String? = null
) : ConnectionSettings
