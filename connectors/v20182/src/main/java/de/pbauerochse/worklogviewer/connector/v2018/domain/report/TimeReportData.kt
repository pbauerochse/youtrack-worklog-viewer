package de.pbauerochse.worklogviewer.connector.v2018.domain.report

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimeReportData @JsonCreator constructor(
    @JsonProperty("groups") val groups: List<TimeReportGroup>,
    @JsonProperty("duration") val duration: TimeReportDuration
)