package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class OwnedBundleElement @JsonCreator constructor(
    @JsonProperty("owner") val owner: User?,
    @JsonProperty("name") val name: String
)

