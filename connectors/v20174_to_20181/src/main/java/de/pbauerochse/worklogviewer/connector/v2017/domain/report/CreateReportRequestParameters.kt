package de.pbauerochse.worklogviewer.connector.v2017.domain.report

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.pbauerochse.worklogviewer.report.TimeReportParameters

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateReportRequestParameters(private val reportParameters: TimeReportParameters) {

    @get:JsonProperty("\$type")
    val youtrackType = "jetbrains.charisma.smartui.report.time.TimeReport"

    @get:JsonProperty("type")
    val type = "time"

    @get:JsonProperty("own")
    val own = true

    @get:JsonProperty("name")
    val name : String by lazy {
        val timerange = reportParameters.timerange
        "YTWLV: ${timerange.reportName}"
    }

    @get:JsonProperty("range")
    val range : FixedTimeRange by lazy {
        FixedTimeRange(
            reportParameters.timerange.start,
            reportParameters.timerange.end
        )
    }

    @get:JsonProperty("grouping")
    val grouping : Grouping? by lazy {
        if (reportParameters.groupByParameter is Grouping) {
            reportParameters.groupByParameter as Grouping
        } else {
            null
        }
    }
}