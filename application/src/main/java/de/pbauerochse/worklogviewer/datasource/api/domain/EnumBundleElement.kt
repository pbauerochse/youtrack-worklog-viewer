package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
open class EnumBundleElement @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("localizedName") val localizedName: String?
)
