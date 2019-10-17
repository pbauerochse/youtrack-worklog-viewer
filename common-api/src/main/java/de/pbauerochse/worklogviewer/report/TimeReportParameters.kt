package de.pbauerochse.worklogviewer.report

/**
 * Contains the parameters needed
 * to generate the [TimeReport]
 */
data class TimeReportParameters(

    /**
     * The timerange to generate
     * the report for
     *
     * Might be a fixed on like "LAST_WEEK" or "LAST_MONTH"
     * or a custom one defining the start- and end date
     */
    val timerange: TimeRange
)