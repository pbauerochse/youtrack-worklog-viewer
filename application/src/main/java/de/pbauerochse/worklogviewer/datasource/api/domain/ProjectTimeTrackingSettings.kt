package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * https://www.jetbrains.com/help/youtrack/devportal/resource-api-admin-projects-projectID-timeTrackingSettings.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectTimeTrackingSettings @JsonCreator constructor(
    @JsonProperty("enabled") val enabled: Boolean,
    @JsonProperty("estimate") val estimate : ProjectCustomField?,
    @JsonProperty("timeSpent") val timeSpent : ProjectCustomField?,
    @JsonProperty("workItemTypes") val workItemTypes: List<YouTrackWorkItemType>
)
