package de.pbauerochse.worklogviewer.datasource.api.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class StateBundleElement @JsonCreator constructor(
    @JsonProperty("name") name: String,
    @JsonProperty("localizedName") localizedName: String?,
    @JsonProperty("isResolved") val isResolved: Boolean
): EnumBundleElement(name, localizedName)
