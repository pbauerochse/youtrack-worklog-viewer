package de.pbauerochse.worklogviewer.connector.v2018.domain.groupby

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class PredefinedFilterField @JsonCreator constructor(
    @JsonProperty("id") override val id: String,
    @JsonProperty("presentation") override val presentation: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("aggregateable") val aggregateable: Boolean,
    @JsonProperty("sortable") val sortable: Boolean
) : GroupingField