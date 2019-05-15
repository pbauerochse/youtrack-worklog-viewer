package de.pbauerochse.worklogviewer.connector.v2019.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.ZoneId

@JsonInclude(JsonInclude.Include.NON_NULL)
class YouTrackCreateWorkItemRequest(
    workDate: LocalDate,
    durationInMinutes: Long,
    @JsonProperty("author") val author: YouTrackUser,
    @JsonProperty("text") val text: String?
) {

    @JsonProperty("date")
    val date: Long = workDate
        .atStartOfDay(ZoneId.of("UTC"))
        .toInstant()
        .toEpochMilli()

    @JsonProperty("duration")
    val duration: YouTrackWorkItemDuration = YouTrackWorkItemDuration(durationInMinutes)
}