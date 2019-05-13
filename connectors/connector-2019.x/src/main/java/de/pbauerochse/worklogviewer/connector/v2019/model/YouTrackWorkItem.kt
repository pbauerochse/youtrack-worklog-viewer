package de.pbauerochse.worklogviewer.connector.v2019.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackWorkItem @JsonCreator constructor(
    @JsonProperty("author") val author : YouTrackUser?,
    @JsonProperty("creator") val creator : YouTrackUser?,
    @JsonProperty("text") val text : String?,
    @JsonProperty("type") val type : YouTrackWorkItemType?,
    @JsonProperty("duration") val duration : YouTrackWorkItemDuration,
    @JsonProperty("date") val dateTimestamp : Long?,
    @JsonProperty("issue") val issue : YouTrackIssue
) {

    val date : ZonedDateTime?
    @JsonIgnore get() = dateTimestamp?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
    }
}