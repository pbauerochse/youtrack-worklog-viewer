package de.pbauerochse.worklogviewer.connector.v2017.domain.report

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class ReportDetails @JsonCreator constructor(
    @JsonProperty("id") val id : String,
    @JsonProperty("status") val status : ReportStatus
) {

    val inProgress : Boolean by lazy {
        status.calculating || status.progress != -1
    }

}