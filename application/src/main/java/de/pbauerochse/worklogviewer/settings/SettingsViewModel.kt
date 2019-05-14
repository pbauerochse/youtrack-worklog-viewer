package de.pbauerochse.worklogviewer.settings

import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.fx.Theme
import de.pbauerochse.worklogviewer.timerange.TimerangeProvider
import de.pbauerochse.worklogviewer.toURL
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import java.time.DayOfWeek.*
import java.time.LocalDate

/**
 * Java FX Model for the settings screen
 */
class SettingsViewModel internal constructor(val settings: Settings) {

    val youTrackUrlProperty = SimpleStringProperty()
    val youTrackVersionProperty = SimpleObjectProperty<YouTrackVersion>()
    val youTrackUsernameProperty = SimpleStringProperty()
    val youTrackPermanentTokenProperty = SimpleStringProperty()

    val themeProperty = SimpleObjectProperty<Theme>()
    val workhoursProperty = SimpleFloatProperty()
    val showAllWorklogsProperty = SimpleBooleanProperty()
    val showStatisticsProperty = SimpleBooleanProperty()
    val loadDataAtStartupProperty = SimpleBooleanProperty()
    val showDecimalsInExcelProperty = SimpleBooleanProperty()
    val enablePluginsProperty = SimpleBooleanProperty()
    val lastUsedReportTimerangeProperty = SimpleObjectProperty<TimerangeProvider>()
    val startDateProperty = SimpleObjectProperty<LocalDate>()
    val endDateProperty = SimpleObjectProperty<LocalDate>()
    val lastUsedGroupByCategoryIdProperty = SimpleStringProperty()
    val lastUsedFilePath = SimpleStringProperty()

    val collapseStateMondayProperty = SimpleBooleanProperty()
    val collapseStateTuesdayProperty = SimpleBooleanProperty()
    val collapseStateWednesdayProperty = SimpleBooleanProperty()
    val collapseStateThursdayProperty = SimpleBooleanProperty()
    val collapseStateFridayProperty = SimpleBooleanProperty()
    val collapseStateSaturdayProperty = SimpleBooleanProperty()
    val collapseStateSundayProperty = SimpleBooleanProperty()

    val highlightStateMondayProperty = SimpleBooleanProperty()
    val highlightStateTuesdayProperty = SimpleBooleanProperty()
    val highlightStateWednesdayProperty = SimpleBooleanProperty()
    val highlightStateThursdayProperty = SimpleBooleanProperty()
    val highlightStateFridayProperty = SimpleBooleanProperty()
    val highlightStateSaturdayProperty = SimpleBooleanProperty()
    val highlightStateSundayProperty = SimpleBooleanProperty()

    val hasMissingConnectionSettings = hasMissingConnectionSettingsBinding

    init {
        applyPropertiesFromSettings()
        bindAutoUpdatingProperties()
    }

    fun saveChanges() {
        settings.youTrackConnectionSettings.baseUrl = youTrackUrlProperty.get().toURL()
        settings.youTrackConnectionSettings.version = youTrackVersionProperty.get()
        settings.youTrackConnectionSettings.username = youTrackUsernameProperty.get()
        settings.youTrackConnectionSettings.permanentToken = youTrackPermanentTokenProperty.get()

        settings.theme = themeProperty.get()
        settings.workHoursADay = workhoursProperty.get()
        settings.isShowAllWorklogs = showAllWorklogsProperty.get()
        settings.isShowStatistics = showStatisticsProperty.get()
        settings.isLoadDataAtStartup = loadDataAtStartupProperty.get()
        settings.isShowDecimalHourTimesInExcelReport = showDecimalsInExcelProperty.get()
        settings.isEnablePlugins = enablePluginsProperty.get()
        settings.lastUsedReportTimerange = lastUsedReportTimerangeProperty.get()
        settings.startDate = startDateProperty.get()
        settings.endDate = endDateProperty.get()
        settings.lastUsedGroupByCategoryId = lastUsedGroupByCategoryIdProperty.get()

        settings.collapseState.set(MONDAY, collapseStateMondayProperty.get())
        settings.collapseState.set(TUESDAY, collapseStateTuesdayProperty.get())
        settings.collapseState.set(WEDNESDAY, collapseStateWednesdayProperty.get())
        settings.collapseState.set(THURSDAY, collapseStateThursdayProperty.get())
        settings.collapseState.set(FRIDAY, collapseStateFridayProperty.get())
        settings.collapseState.set(SATURDAY, collapseStateSaturdayProperty.get())
        settings.collapseState.set(SUNDAY, collapseStateSundayProperty.get())

        settings.highlightState.set(MONDAY, highlightStateMondayProperty.get())
        settings.highlightState.set(TUESDAY, highlightStateTuesdayProperty.get())
        settings.highlightState.set(WEDNESDAY, highlightStateWednesdayProperty.get())
        settings.highlightState.set(THURSDAY, highlightStateThursdayProperty.get())
        settings.highlightState.set(FRIDAY, highlightStateFridayProperty.get())
        settings.highlightState.set(SATURDAY, highlightStateSaturdayProperty.get())
        settings.highlightState.set(SUNDAY, highlightStateSundayProperty.get())

        SettingsUtil.saveSettings()
    }

    fun discardChanges() {
        applyPropertiesFromSettings()
    }

    private fun applyPropertiesFromSettings() {
        youTrackUrlProperty.set(settings.youTrackConnectionSettings.baseUrl?.toExternalForm())
        youTrackVersionProperty.set(settings.youTrackConnectionSettings.version)
        youTrackUsernameProperty.set(settings.youTrackConnectionSettings.username)
        youTrackPermanentTokenProperty.set(settings.youTrackConnectionSettings.permanentToken)

        themeProperty.set(settings.theme)
        workhoursProperty.set(settings.workHoursADay)
        showAllWorklogsProperty.set(settings.isShowAllWorklogs)
        showStatisticsProperty.set(settings.isShowStatistics)
        loadDataAtStartupProperty.set(settings.isLoadDataAtStartup)
        showDecimalsInExcelProperty.set(settings.isShowDecimalHourTimesInExcelReport)
        enablePluginsProperty.set(settings.isEnablePlugins)
        lastUsedReportTimerangeProperty.set(settings.lastUsedReportTimerange)
        lastUsedFilePath.set(settings.lastUsedFilePath)
        startDateProperty.set(settings.startDate)
        endDateProperty.set(settings.endDate)
        lastUsedGroupByCategoryIdProperty.set(settings.lastUsedGroupByCategoryId)

        collapseStateMondayProperty.set(settings.collapseState.isSet(MONDAY))
        collapseStateTuesdayProperty.set(settings.collapseState.isSet(TUESDAY))
        collapseStateWednesdayProperty.set(settings.collapseState.isSet(WEDNESDAY))
        collapseStateThursdayProperty.set(settings.collapseState.isSet(THURSDAY))
        collapseStateFridayProperty.set(settings.collapseState.isSet(FRIDAY))
        collapseStateSaturdayProperty.set(settings.collapseState.isSet(SATURDAY))
        collapseStateSundayProperty.set(settings.collapseState.isSet(SUNDAY))

        highlightStateMondayProperty.set(settings.highlightState.isSet(MONDAY))
        highlightStateTuesdayProperty.set(settings.highlightState.isSet(TUESDAY))
        highlightStateWednesdayProperty.set(settings.highlightState.isSet(WEDNESDAY))
        highlightStateThursdayProperty.set(settings.highlightState.isSet(THURSDAY))
        highlightStateFridayProperty.set(settings.highlightState.isSet(FRIDAY))
        highlightStateSaturdayProperty.set(settings.highlightState.isSet(SATURDAY))
        highlightStateSundayProperty.set(settings.highlightState.isSet(SUNDAY))
    }

    private val hasMissingConnectionSettingsBinding: BooleanBinding
        get() = youTrackUrlProperty.isEmpty
            .or(youTrackVersionProperty.isNull)
            .or(youTrackUsernameProperty.isEmpty)
            .or(youTrackPermanentTokenProperty.isEmpty)

    /**
     * These settings are applied to the persistent settings
     * object whenever they are changed. In general, those are
     * the application state properties, that are not set
     * in the settings view.
     */
    private fun bindAutoUpdatingProperties() {
        lastUsedReportTimerangeProperty.addListener(invokeSetter { settings.lastUsedReportTimerange = it })
        lastUsedGroupByCategoryIdProperty.addListener(invokeSetter { settings.lastUsedGroupByCategoryId = it })
        startDateProperty.addListener(invokeSetter { settings.startDate = it })
        endDateProperty.addListener(invokeSetter { settings.endDate = it })
        lastUsedFilePath.addListener(invokeSetter { settings.lastUsedFilePath = it })
    }

    private fun <T> invokeSetter(block: (t : T) -> Unit): ChangeListener<T> {
        return ChangeListener { _, _, newValue -> block.invoke(newValue) }
    }

}
