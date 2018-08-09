package de.pbauerochse.worklogviewer.connector.v2017.domain.groupby

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "\$type", defaultImpl = UnknownFilterField::class)
@JsonSubTypes(
    JsonSubTypes.Type(value = GroupByTypes::class, name = "jetbrains.charisma.smartui.report.time.GroupByTypes"),
    JsonSubTypes.Type(value = PredefinedFilterField::class, name = "jetbrains.charisma.keyword.PredefinedFilterField"),
    JsonSubTypes.Type(value = CustomFilterField::class, name = "jetbrains.charisma.keyword.CustomFilterField")
)
interface GroupingField {

    val id: String
    val presentation: String
    val isProcessableFieldGrouping: Boolean
    fun getLabel(): String = presentation

}