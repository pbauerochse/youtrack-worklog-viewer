package de.pbauerochse.worklogviewer.connector.v2017.domain.groupby

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.pbauerochse.worklogviewer.connector.GroupByParameter

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "\$type")
@JsonSubTypes(
    JsonSubTypes.Type(value = GroupByTypes::class, name = "jetbrains.charisma.smartui.report.time.GroupByTypes"),
    JsonSubTypes.Type(value = PredefinedFilterField::class, name = "jetbrains.charisma.keyword.PredefinedFilterField"),
    JsonSubTypes.Type(value = CustomFilterField::class, name = "jetbrains.charisma.keyword.CustomFilterField")
)
interface GroupingField : GroupByParameter {

    override val id: String
    val presentation: String

    override fun getLabel(): String = presentation

}