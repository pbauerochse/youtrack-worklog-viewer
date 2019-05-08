package de.pbauerochse.worklogviewer.plugins.formatter

interface YouTrackWorktimeFormatter {

    /**
     * returns the [durationInMinutes] formatted in the YouTrack / JIRA format
     * as string:
     *
     * ```
     * 60  -> 1h
     * 120 -> 2h
     * 135 -> 1h 15m
     * 480 -> 1d
     * ```
     *
     * @param durationInMinutes     the duration in minutes to be formatted
     * @param full                  always output minutes and hours, even if the date perfectly resolves to a day (false : 480 -> 1d, true : 480 -> 1d 0h 0m)
     */
    fun getFormatted(durationInMinutes : Long, full : Boolean = false) : String

}