package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class VersionBundleElement @JsonCreator constructor(
    @JsonProperty("archived") val archived: Boolean,
    @JsonProperty("releaseDate") val releaseDateTimestamp: Long?,
    @JsonProperty("released") val released: Boolean,
    @JsonProperty("name") val name: String
)

