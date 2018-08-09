package de.pbauerochse.worklogviewer.connector.v2018.domain.groupby

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Represents an unknown GroupingField subtype
 * which will be ignored
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class UnknownFilterField : GroupingField {

    override val id: String = "UNKNOWN"
    override val presentation: String = "UNKNOWN"
    override fun getPossibleNames(): Iterable<String> = emptyList()
    override val isProcessableFieldGrouping: Boolean = false

}
