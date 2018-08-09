package de.pbauerochse.worklogviewer.connector.v2017.domain.report

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class UnknownGrouping : Grouping {

    override val id: String = "UNKNONW"
    override fun getLabel(): String = "UNKNOWN"

}