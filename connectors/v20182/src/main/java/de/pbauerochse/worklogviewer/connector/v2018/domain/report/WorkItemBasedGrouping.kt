package de.pbauerochse.worklogviewer.connector.v2018.domain.report

import com.fasterxml.jackson.annotation.*
import de.pbauerochse.worklogviewer.connector.v2018.domain.groupby.GroupingField

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class WorkItemBasedGrouping @JsonCreator constructor(
    @JsonProperty("field") val field: GroupingField
) : Grouping {

    @JsonIgnore
    override val id: String = field.id

    @JsonIgnore
    override fun getLabel(): String = field.presentation
}