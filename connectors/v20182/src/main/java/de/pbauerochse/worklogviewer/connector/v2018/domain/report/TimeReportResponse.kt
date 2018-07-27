package de.pbauerochse.worklogviewer.connector.v2018.domain.report

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import de.pbauerochse.worklogviewer.connector.v2018.domain.grouping.Grouping

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimeReportResponse @JsonCreator constructor(
    @JsonProperty("grouping") val grouping: Grouping?,
    @JsonProperty("data") val data: TimeReportData
)