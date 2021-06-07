package de.pbauerochse.worklogviewer.fx.components.statistics.panels

import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByProjectAndUserStatisticData
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.StackedBarChart
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.slf4j.LoggerFactory

/**
 * Displays the time spent by a user as a bar graph
 * Each bar is divided into sections fpr the different
 * projects, the user worked on
 */
class TimePerUserAndProjectGraphStatistics(
    private val statisticsData: TaskCountByProjectAndUserStatisticData,
    employeeAxis: NumberAxis = NumberAxis()
) : StackedBarChart<Number, String>(employeeAxis, CategoryAxis()) {

    private var alreadyRendered = false

    init {
        title = getFormatted("view.statistics.byemployeeandproject")
        prefHeight = (HEIGHT_PER_Y_AXIS_ELEMENT * statisticsData.numberOfUsers + HEIGHT_PER_X_AXIS_ELEMENT * statisticsData.projectStatistic.size + ADDITIONAL_HEIGHT).toDouble()

        employeeAxis.apply {
            label = getFormatted("view.statistics.timespentinhours")
            tickLabelRotation = 90.0
        }

        VBox.setVgrow(this, Priority.ALWAYS)

        renderStatisticsIfNecessary()
        visibleProperty().addListener { _, _, _ -> renderStatisticsIfNecessary() }
    }

    private fun renderStatisticsIfNecessary() {
        if (isVisible && !alreadyRendered) {
            LOGGER.debug("Rendering Data")
            renderStatistics()
            alreadyRendered = true
        }
    }

    private fun renderStatistics() {
        statisticsData.projectStatistic.forEach { projectStatistic ->
            val series = Series<Number, String>()
            series.name = projectStatistic.projectName

            projectStatistic.userStatistics.forEach { userSummary ->
                val timeSpentInHours = userSummary.timeSpentInMinutes.toDouble() / 60.0
                val formattedTime = formatMinutes(userSummary.timeSpentInMinutes)
                val data = Data<Number, String>(timeSpentInHours, userSummary.user.label)
                series.data.add(data)

                data.nodeProperty().addListener { _, _, newNode -> Tooltip.install(newNode, Tooltip("${series.name} - $formattedTime")) }
            }

            data.add(series)
        }
    }

    companion object {
        private const val HEIGHT_PER_Y_AXIS_ELEMENT = 40
        private const val HEIGHT_PER_X_AXIS_ELEMENT = 35
        private const val ADDITIONAL_HEIGHT = 150
        private val LOGGER = LoggerFactory.getLogger(TimePerUserAndProjectGraphStatistics::class.java)
    }
}