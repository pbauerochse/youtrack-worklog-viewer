package de.pbauerochse.worklogviewer.connector.v2017.domain.report

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class ReportStatus @JsonCreator constructor(
    @JsonProperty("calculationInProgress") val calculating : Boolean,
    @JsonProperty("progress") val progress : Int
)