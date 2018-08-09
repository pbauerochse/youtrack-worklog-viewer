package de.pbauerochse.worklogviewer.connector.v2017.domain.issuedetails

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * The YouTrack API response object
 * when fetching the details for a
 * bunch if Issues
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class IssueDetailsResponse @JsonCreator constructor(
    @JsonProperty("issue") val issues : List<IssueDetails>
)