package de.pbauerochse.worklogviewer.connector.v2018.domain.issue

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.pbauerochse.worklogviewer.toLocalDate
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class YouTrackWorklogItem @JsonCreator constructor(
    @JsonProperty("date") val date: Long,
    @JsonProperty("duration") val duration: Long,
    @JsonProperty("author") val author: WorklogItemAuthor,
    @JsonProperty("description") val description: String?,
    @JsonProperty("worktype") val worktype: WorklogItemWorktype?
) {

    val localDate: LocalDate = date.toLocalDate()

}