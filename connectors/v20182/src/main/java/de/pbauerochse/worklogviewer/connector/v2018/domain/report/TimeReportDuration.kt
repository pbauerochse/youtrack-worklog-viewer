package de.pbauerochse.worklogviewer.connector.v2018.domain.report

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimeReportDuration @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("presentation") val presentation: String,
    @JsonProperty("minutes") val minutes: Long
)