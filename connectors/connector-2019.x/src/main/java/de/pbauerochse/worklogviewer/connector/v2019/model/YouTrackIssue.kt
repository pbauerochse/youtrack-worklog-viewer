package de.pbauerochse.worklogviewer.connector.v2019.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackIssue @JsonCreator constructor(
    @JsonProperty("idReadable") val id : String,
    @JsonProperty("project") val project : YouTrackProject?,
    @JsonProperty("resolved") val resolved : Long?,
    @JsonProperty("summary") val summary : String?,
    @JsonProperty("customFields") val customFields : List<YouTrackCustomField>
) {

    val resolveDate : ZonedDateTime?
        get() = resolved?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
        }

}