package de.pbauerochse.worklogviewer.fx.components.statistics.panels

import de.pbauerochse.worklogviewer.isSameDayOrBefore
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.settings.SettingsViewModel
import de.pbauerochse.worklogviewer.timereport.TimeRange
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.absoluteValue

/**
 * Statistics Pane that shows the overtime you have
 * worked within the given report timerange, by checking
 * the total booked time, against the defined workhours per
 * day setting
 */
class OvertimeStatisticsPane(private val reportView: ReportView) : GridPane() {

    lateinit var ignoreWeekends: CheckBox
    lateinit var ignoreDaysWithoutWorklogItems: CheckBox
    lateinit var ignoreToday: CheckBox

    lateinit var totalDays: Label
    lateinit var totalDaysWithTimeEntries: Label
    lateinit var totalDaysWithoutTimeEntries: Label
    lateinit var totalDaysWithIncompleteTimeEntries: Label
    lateinit var totalDaysWithOverfilledTimeEntries: Label
    lateinit var totalBookedTime: Label
    lateinit var expectedTotalBookedTime: Label
    lateinit var diffBookedTime: Label

    init {
        val loader = FXMLLoader(this::class.java.getResource("/fx/components/statistics/overtime.fxml"), FormattingUtil.RESOURCE_BUNDLE)
        loader.setRoot(this)
        loader.setController(this)
        loader.load<Parent>()

        val settingsViewModel = SettingsUtil.settingsViewModel

        ignoreWeekends.selectedProperty().bindBidirectional(settingsViewModel.overtimeStatisticsIgnoreWeekendsProperty)
        ignoreWeekends.selectedProperty().addListener { _, _, _ -> updateData(settingsViewModel) }
        ignoreDaysWithoutWorklogItems.selectedProperty().bindBidirectional(settingsViewModel.overtimeStatisticsIgnoreWithoutTimeEntriesProperty)
        ignoreDaysWithoutWorklogItems.selectedProperty().addListener { _, _, _ -> updateData(settingsViewModel) }
        ignoreToday.selectedProperty().bindBidirectional(settingsViewModel.overtimeStatisticsIgnoreTodayProperty)
        ignoreToday.selectedProperty().addListener { _, _, _ -> updateData(settingsViewModel) }

        updateData(settingsViewModel)
    }

    private fun updateData(settingsViewModel: SettingsViewModel) {
        val expectedWorkMinutesPerDay = settingsViewModel.workhoursProperty.value.times(60).toLong()
        val timerangeOfPastDaysOnly = getTimerangeWithoutUpcomingDays(reportView)

        val relevantDaysInTimeRange = timerangeOfPastDaysOnly.toMutableList()

        if (ignoreWeekends.isSelected) {
            relevantDaysInTimeRange.removeAll { it.dayOfWeek == DayOfWeek.SATURDAY || it.dayOfWeek == DayOfWeek.SUNDAY }
        }

        if (ignoreDaysWithoutWorklogItems.isSelected) {
            relevantDaysInTimeRange.removeAll { day -> reportView.issues.none { it.getWorkItemsForDate(day).isNotEmpty() } }
        }

        if (ignoreToday.isSelected) {
            relevantDaysInTimeRange.remove(LocalDate.now())
        }

        totalDays.text = "${relevantDaysInTimeRange.size}"
        totalDaysWithTimeEntries.text = relevantDaysInTimeRange.count { date -> reportView.issues.any { it.getWorkItemsForDate(date).isNotEmpty() } }.toString()
        totalDaysWithoutTimeEntries.text = relevantDaysInTimeRange.count { date ->  reportView.issues.none { it.getWorkItemsForDate(date).isNotEmpty() } }.toString()
        totalDaysWithIncompleteTimeEntries.text = relevantDaysInTimeRange.count { date ->
            val totalTimeSpentThisDay = reportView.issues.sumOf { it.getWorkItemsForDate(date).sumOf { workItem -> workItem.durationInMinutes } }
            // more than 0, less than expectedWorkMinutesPerDay
            return@count totalTimeSpentThisDay in 1 until expectedWorkMinutesPerDay
        }.toString()
        totalDaysWithOverfilledTimeEntries.text = relevantDaysInTimeRange.count { date ->
            val totalTimeSpentThisDay = reportView.issues.sumOf { it.getWorkItemsForDate(date).sumOf { workItem -> workItem.durationInMinutes } }
            return@count totalTimeSpentThisDay > expectedWorkMinutesPerDay
        }.toString()

        val totalBookedTimeInMinutes = relevantDaysInTimeRange.flatMap { date -> reportView.issues.map { it.getWorkItemsForDate(date).sumOf { workItem -> workItem.durationInMinutes } } }.sum()
        val expectedTotalBookedTimeInMinutes = relevantDaysInTimeRange.size.times(settingsViewModel.workhoursProperty.value).times(60).toLong()

        totalBookedTime.text = formatMinutes(totalBookedTimeInMinutes)
        expectedTotalBookedTime.text = formatMinutes(expectedTotalBookedTimeInMinutes)


        val diff = totalBookedTimeInMinutes - expectedTotalBookedTimeInMinutes
        val isFilledOrOvertime = diff >= 0

        val sign = when {
            diff > 0 -> "+"
            diff < 0 -> "-"
            else -> "+/-"
        }
        val formattedValue = when {
            diff == 0L -> "0"
            else -> formatMinutes(diff.absoluteValue)
        }

        diffBookedTime.apply {
            text = "$sign $formattedValue"
            styleClass.removeAll(UNDERTIME_CLASS, OVERTIME_CLASS)
            styleClass.add(if (isFilledOrOvertime) OVERTIME_CLASS else UNDERTIME_CLASS)
        }
    }

    private fun getTimerangeWithoutUpcomingDays(reportView: ReportView): TimeRange {
        val today = LocalDate.now()
        val reportTimerange = reportView.reportParameters.timerange

        // only consider past days and today
        val normalizedStartDate = reportTimerange.start.takeIf { it.isSameDayOrBefore(today) } ?: today
        val normalizedEndDate = reportTimerange.end.takeIf { it.isSameDayOrBefore(today) } ?: today
        return TimeRange(normalizedStartDate, normalizedEndDate)
    }

    companion object {
        const val UNDERTIME_CLASS = "negative"
        const val OVERTIME_CLASS = "positive"
    }

}