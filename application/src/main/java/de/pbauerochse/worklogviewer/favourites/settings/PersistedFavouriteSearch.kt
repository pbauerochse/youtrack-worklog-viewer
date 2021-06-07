package de.pbauerochse.worklogviewer.favourites.settings

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PersistedFavouriteSearch @JsonCreator constructor(
    @JsonProperty("title") var name: String,
    @JsonProperty("query") var query: String
)
