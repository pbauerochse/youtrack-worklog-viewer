package de.pbauerochse.worklogviewer.connector.v2018.domain.issue

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.ZoneId

@JsonInclude(JsonInclude.Include.NON_NULL)
class YouTrackAddWorkItemRequest(
    workDate: LocalDate,
    @JsonProperty("duration") val durationInMinutes: Long,
    @JsonProperty("description") val description: String?
) {

    @JsonProperty("date")
    val date: Long = workDate
        .atStartOfDay(ZoneId.of("UTC"))
        .toInstant()
        .toEpochMilli()

}