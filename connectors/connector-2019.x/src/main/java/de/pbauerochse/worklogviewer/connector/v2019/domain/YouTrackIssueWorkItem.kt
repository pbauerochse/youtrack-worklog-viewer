package de.pbauerochse.worklogviewer.connector.v2019.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * https://www.jetbrains.com/help/youtrack/devportal/resource-api-workItems.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackIssueWorkItem @JsonCreator constructor(
    @JsonProperty("author") val author: YouTrackUser?,
    @JsonProperty("creator") val creator: YouTrackUser?,
    @JsonProperty("text") val text: String?,
    @JsonProperty("type") val type: YouTrackWorkItemType?,
    @JsonProperty("duration") val duration: YouTrackWorkItemDuration,
    @JsonProperty("date") private val dateTimestamp: Long,
    @JsonProperty("issue") val issue: YouTrackIssue
) {

    @get:JsonIgnore
    val date: ZonedDateTime
        get() = dateTimestamp.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
        }
}