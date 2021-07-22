package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * https://www.jetbrains.com/help/youtrack/devportal/api-entity-Issue.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackIssue @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("idReadable") val idReadable : String,
    @JsonProperty("resolved") private val resolved : Long?,
    @JsonProperty("project") val project : YouTrackProject?,
    @JsonProperty("summary") val summary : String?,
    @JsonProperty("wikifiedDescription") val description : String,
    @JsonProperty("customFields") val customFields : List<IssueCustomField<*>>,
    @JsonProperty("tags") val tags: List<IssueTag>
) {

    @get:JsonIgnore
    val resolveDate : ZonedDateTime?
        get() = resolved?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
        }

}
