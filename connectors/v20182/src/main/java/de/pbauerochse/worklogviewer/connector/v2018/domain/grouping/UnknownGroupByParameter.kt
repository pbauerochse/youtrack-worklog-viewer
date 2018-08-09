package de.pbauerochse.worklogviewer.connector.v2018.domain.grouping

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.pbauerochse.worklogviewer.connector.GroupByParameter

@JsonIgnoreProperties(ignoreUnknown = true)
class UnknownGroupByParameter : GroupByParameter {
    override val id: String = "UNKNOWN"
    override fun getLabel(): String = "UNKNOWN"
}