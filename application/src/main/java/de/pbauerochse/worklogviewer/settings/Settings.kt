package de.pbauerochse.worklogviewer.settings

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import de.pbauerochse.worklogviewer.fx.Theme
import de.pbauerochse.worklogviewer.settings.favourites.Favourites
import de.pbauerochse.worklogviewer.settings.jackson.*
import de.pbauerochse.worklogviewer.timerange.CurrentWeekTimerangeProvider
import de.pbauerochse.worklogviewer.timerange.TimerangeProvider
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate

/**
 * Contains all settings for the YouTrack Worklog Viewer
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
class Settings {

    /**
     * Settings regarding the main application window
     */
    @JsonProperty("window")
    var windowSettings = WindowSettings()

    /**
     * Any settings required to access the YouTrack API
     */
    @JsonProperty("youtrack")
    var youTrackConnectionSettings = YouTrackConnectionSettingsImpl()

    var theme = Theme.DARK

    /**
     * The amount of hours the user has to work on
     * any given day. Determines, how the YouTrack
     * times are being displayed:
     *
     *
     * - 9h booked hours on a 8h workday -> 1d 1h
     * - 9h booked hours on a 6h workday -> 1d 3h
     * - 9h booked hours on a 9h workday -> 1d
     */
    var workHoursADay = 8.0f

    @JsonSerialize(using = TimerangeProviderSerializer::class)
    @JsonDeserialize(using = TimerangeProviderDeserializer::class)
    var lastUsedReportTimerange: TimerangeProvider = CurrentWeekTimerangeProvider

    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonDeserialize(using = LocalDateDeserializer::class)
    var startDate: LocalDate? = null

    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonDeserialize(using = LocalDateDeserializer::class)
    var endDate: LocalDate? = null

    var lastUsedGroupByCategoryId: String? = null
    var lastUsedFilePath: String? = null
    var isLoadDataAtStartup = false
    var isShowStatistics = true
    var isShowAllWorklogs = true
    var isShowDecimalHourTimesInExcelReport = false
    var isEnablePlugins = false

    @JsonSerialize(using = WeekdaySettingsSerializer::class)
    @JsonDeserialize(using = WeekdaySettingsDeserializer::class)
    var collapseState = WeekdaySettings(SATURDAY, SUNDAY)

    @JsonSerialize(using = WeekdaySettingsSerializer::class)
    @JsonDeserialize(using = WeekdaySettingsDeserializer::class)
    var highlightState = WeekdaySettings(SATURDAY, SUNDAY)

    @JsonProperty("shortcuts")
    var shortcuts : KeyboardShotcuts = KeyboardShotcuts()

    @JsonProperty("favourites")
    var favourites : Favourites = Favourites()

}
