package de.pbauerochse.worklogviewer.settings

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class KeyboardShotcuts {
    var fetchWorklogs: String? = null
    var addWorkitem: String? = null
    var toggleStatistics: String? = null
    var showSettings: String? = null
    var exitWorklogViewer: String? = null
}
