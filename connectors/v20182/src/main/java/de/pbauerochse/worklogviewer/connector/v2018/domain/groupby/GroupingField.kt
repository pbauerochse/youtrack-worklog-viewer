package de.pbauerochse.worklogviewer.connector.v2018.domain.groupby

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "\$type")
@JsonSubTypes(
    JsonSubTypes.Type(value = GroupByTypes::class, name = "jetbrains.charisma.smartui.report.time.GroupByTypes"),
    JsonSubTypes.Type(value = PredefinedFilterField::class, name = "jetbrains.charisma.keyword.PredefinedFilterField"),
    JsonSubTypes.Type(value = CustomFilterField::class, name = "jetbrains.charisma.keyword.CustomFilterField")
)
interface GroupingField {

    val id: String
    val presentation: String

    /**
     * Some GroupingFields supplied by YouTrack can not
     * be reliably used to group the issues. So they need
     * to be filtered out and / or replace by own implementations
     */
    fun isProcessableFieldGrouping(): Boolean = this !is PredefinedFilterField

    fun getLabel(): String = presentation

    fun getPossibleNames(): Iterable<String>

}