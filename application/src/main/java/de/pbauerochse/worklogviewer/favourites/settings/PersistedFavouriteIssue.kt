package de.pbauerochse.worklogviewer.favourites.settings

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PersistedFavouriteIssue @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("summary") val summary: String
)
