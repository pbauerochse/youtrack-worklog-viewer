package de.pbauerochse.worklogviewer.connector.v2017.domain.report

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.GroupingField

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class FieldBasedGrouping @JsonCreator constructor(
    @JsonProperty("field") val field: GroupingField
) : Grouping {

    @JsonProperty("id")
    override val id: String = field.id

    @JsonProperty("presentation")
    override fun getLabel(): String = field.presentation

}