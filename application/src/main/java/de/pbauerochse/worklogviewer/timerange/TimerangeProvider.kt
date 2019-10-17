package de.pbauerochse.worklogviewer.timerange

import de.pbauerochse.worklogviewer.report.TimeRange
import java.time.LocalDate

/**
 * Provides the start- and enddate
 * for a given timerange
 */
interface TimerangeProvider {

    /**
     * @return The human readable label of this timerange
     */
    val label: String

    /**
     * @return the constant key that will be stored in the settings file
     */
    val settingsKey: String

    /**
     * If `true`, the range can be calculated without
     * input by the user (e.g. "Last week") whereas "Custom"
     * range requires the user to define the start and end date
     */
    val isComputed : Boolean

    /**
     * @return creates the appropriate timerange
     * @param start The optional start date. May not be taken into account for computed timeranges
     * @param end The optional end date. May not be taken into account for computed timeranges
     */
    fun buildTimeRange(start : LocalDate? = null, end : LocalDate? = null) : TimeRange

}
