package de.pbauerochse.worklogviewer.connector.v2018.domain.issue

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * The YouTrack API response object
 * when fetching a bunch of Issues
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class IssueDetailsResponse @JsonCreator constructor(
    @JsonProperty("issue") val issues: List<YouTrackIssue>
)