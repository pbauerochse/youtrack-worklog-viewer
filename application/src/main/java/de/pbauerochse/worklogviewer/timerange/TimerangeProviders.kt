package de.pbauerochse.worklogviewer.timerange

/**
 * Methods / Fields related to [TimerangeProvider]s
 */
object TimerangeProviders {

    /**
     * Lists all [TimerangeProvider]s available
     */
    @JvmStatic
    val allTimerangeProviders
        get() = listOf(
            LastTwoWeeksTimerangeProvider,
            LastWeekTimerangeProvider,
            CurrentAndLastWeekTimerangeProvider,
            CurrentWeekTimerangeProvider,
            CurrentMonthTimerangeProvider,
            LastMonthTimerangeProvider,
            CustomTimerangeProvider
        )

    @JvmStatic
    fun fromKey(timerangeProviderKey: String?): TimerangeProvider? = allTimerangeProviders
        .firstOrNull { it.settingsKey == timerangeProviderKey }
}
