package de.pbauerochse.worklogviewer.connector.v2017.domain.groupby

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class UnknownFilterField : GroupingField {
    override val id: String = "UNKNOWN"
    override val presentation: String = "UNKNOWN"
    override val isProcessableFieldGrouping: Boolean = false
}