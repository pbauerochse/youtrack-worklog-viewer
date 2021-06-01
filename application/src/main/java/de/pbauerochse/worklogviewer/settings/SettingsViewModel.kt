package de.pbauerochse.worklogviewer.settings

import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.fx.ConnectorDescriptor
import de.pbauerochse.worklogviewer.fx.Theme
import de.pbauerochse.worklogviewer.settings.favourites.FavouritesModel
import de.pbauerochse.worklogviewer.timerange.TimerangeProvider
import de.pbauerochse.worklogviewer.toURL
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.scene.input.KeyCombination
import java.time.DayOfWeek.*
import java.time.LocalDate

/**
 * Java FX Model for the settings screen
 */
class SettingsViewModel internal constructor(val settings: Settings) {

    val youTrackUrlProperty = SimpleStringProperty()
    val youTrackConnectorProperty = SimpleObjectProperty<ConnectorDescriptor>()
    val youTrackUsernameProperty = SimpleStringProperty()
    val youTrackPermanentTokenProperty = SimpleStringProperty()

    val themeProperty = SimpleObjectProperty<Theme>()
    val workhoursProperty = SimpleFloatProperty()
    val showAllWorklogsProperty = SimpleBooleanProperty()
    val showStatisticsProperty = SimpleBooleanProperty()
    val statisticsPaneDividerPosition = SimpleDoubleProperty()
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

    // shortcuts
    val fetchWorklogsKeyboardCombination = SimpleObjectProperty<KeyCombination>()
    val showIssueSearchKeyboardCombination = SimpleObjectProperty<KeyCombination>()
    val toggleStatisticsKeyboardCombination = SimpleObjectProperty<KeyCombination>()
    val showSettingsKeyboardCombination = SimpleObjectProperty<KeyCombination>()
    val exitWorklogViewerKeyboardCombination = SimpleObjectProperty<KeyCombination>()

    // overtime statistics
    var overtimeStatisticsIgnoreWeekendsProperty = SimpleBooleanProperty()
    var overtimeStatisticsIgnoreWithoutTimeEntriesProperty = SimpleBooleanProperty()
    var overtimeStatisticsIgnoreTodayProperty = SimpleBooleanProperty()

    val favourites : FavouritesModel = FavouritesModel(settings.favourites)

    init {
        applyPropertiesFromSettings()
        bindAutoUpdatingProperties()
    }

    fun saveChanges() {
        settings.youTrackConnectionSettings.baseUrl = youTrackUrlProperty.get().toURL()
        settings.youTrackConnectionSettings.selectedConnectorId = youTrackConnectorProperty.get().id
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

        settings.shortcuts.fetchWorklogs = fetchWorklogsKeyboardCombination.get()?.name
        settings.shortcuts.showIssueSearch = showIssueSearchKeyboardCombination.get()?.name
        settings.shortcuts.toggleStatistics = toggleStatisticsKeyboardCombination.get()?.name
        settings.shortcuts.showSettings = showSettingsKeyboardCombination.get()?.name
        settings.shortcuts.exitWorklogViewer = exitWorklogViewerKeyboardCombination.get()?.name

        SettingsUtil.saveSettings()
    }

    fun discardChanges() {
        applyPropertiesFromSettings()
    }

    private fun applyPropertiesFromSettings() {
        youTrackUrlProperty.set(settings.youTrackConnectionSettings.baseUrl?.toExternalForm())
        youTrackConnectorProperty.set(DataSources.dataSourceFactories.find { it.id == settings.youTrackConnectionSettings.selectedConnectorId }?.let { ConnectorDescriptor(it.id, it.name) })
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

        settings.shortcuts.fetchWorklogs?.let { fetchWorklogsKeyboardCombination.set(KeyCombination.valueOf(it)) }
        settings.shortcuts.showIssueSearch?.let { showIssueSearchKeyboardCombination.set(KeyCombination.valueOf(it)) }
        settings.shortcuts.toggleStatistics?.let { toggleStatisticsKeyboardCombination.set(KeyCombination.valueOf(it)) }
        settings.shortcuts.showSettings?.let { showSettingsKeyboardCombination.set(KeyCombination.valueOf(it)) }
        settings.shortcuts.exitWorklogViewer?.let { exitWorklogViewerKeyboardCombination.set(KeyCombination.valueOf(it)) }

        overtimeStatisticsIgnoreWeekendsProperty.set(settings.isOvertimeStatisticsIgnoreWeekends)
        overtimeStatisticsIgnoreWithoutTimeEntriesProperty.set(settings.isOvertimeStatisticsIgnoreWithoutTimeEntries)
        overtimeStatisticsIgnoreTodayProperty.set(settings.isOvertimeStatisticsIgnoreToday)

        statisticsPaneDividerPosition.set(settings.statisticsPaneDividerPosition)
    }

    private val hasMissingConnectionSettingsBinding: BooleanBinding
        get() = youTrackUrlProperty.isEmpty
            .or(youTrackConnectorProperty.isNull)
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

        overtimeStatisticsIgnoreWeekendsProperty.addListener(invokeSetter { settings.isOvertimeStatisticsIgnoreWeekends = it })
        overtimeStatisticsIgnoreWithoutTimeEntriesProperty.addListener(invokeSetter { settings.isOvertimeStatisticsIgnoreWithoutTimeEntries = it })
        overtimeStatisticsIgnoreTodayProperty.addListener(invokeSetter { settings.isOvertimeStatisticsIgnoreToday = it })

        statisticsPaneDividerPosition.addListener(invokeSetter { settings.statisticsPaneDividerPosition = it.toDouble() })
    }

    private fun <T> invokeSetter(block: (t : T) -> Unit): ChangeListener<T> {
        return ChangeListener { _, _, newValue -> block.invoke(newValue) }
    }

}
