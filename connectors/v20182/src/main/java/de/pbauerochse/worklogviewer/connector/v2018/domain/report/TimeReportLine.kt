package de.pbauerochse.worklogviewer.connector.v2018.domain.report

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimeReportLine @JsonCreator constructor(
    @JsonProperty("issueSummary") val issueSummary: String,
    @JsonProperty("groupName") val groupName: String,
    @JsonProperty("issueId") val issueId: String,
    @JsonProperty("userName") val userName: String?,
    @JsonProperty("description") val description: String,
    @JsonProperty("duration") val duration: TimeReportDuration,
    @JsonProperty("estimation") val estimation: TimeReportEstimation,
    @JsonProperty("name") val name: String
)