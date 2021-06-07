package de.pbauerochse.worklogviewer.fx.state

import de.pbauerochse.worklogviewer.timereport.TimeReport
import javafx.beans.property.SimpleObjectProperty

/**
 * For static access to the current [TimeReport]
 * being displayed
 */
object ReportDataHolder {
    val currentTimeReportProperty = SimpleObjectProperty<TimeReport?>(null)
}