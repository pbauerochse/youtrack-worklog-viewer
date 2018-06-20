package de.pbauerochse.worklogviewer.fx.components.statistics.panels

import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatMinutes
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.StackedBarChart
import javafx.scene.chart.XYChart
import javafx.scene.control.Tooltip
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.slf4j.LoggerFactory

/**
 * Displays the time spent on a project
 * as a bar graph. The bar contains a section
 * for each user that spent some time on
 * the project, according to the booked
 * worklog items
 */
class TimePerProjectAndUserGraphStatistics(
    private val statisticsData: TaskCountByUserAndProjectStatisticData,
    projectAxis: NumberAxis = NumberAxis()
) : StackedBarChart<Number, String>(projectAxis, CategoryAxis()) {

    private var alreadyRendered = false

    init {
        title = getFormatted("view.statistics.byprojectandemployee")
        prefHeight = (HEIGHT_PER_Y_AXIS_ELEMENT * statisticsData.numberOfProjects + HEIGHT_PER_X_AXIS_ELEMENT * statisticsData.userStatistics.size + ADDITIONAL_HEIGHT).toDouble()

        projectAxis.apply {
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
        statisticsData.userStatistics.forEach {
            val series = XYChart.Series<Number, String>()
            series.name = it.userDisplayLabel

            it.projectSummaries.forEach {
                val timeSpentInHours = it.timeSpentInMinutes.toDouble() / 60.0
                val formattedTime = formatMinutes(it.timeSpentInMinutes)
                val data = XYChart.Data<Number, String>(timeSpentInHours, it.projectId)
                series.data.add(data)

                data.nodeProperty().addListener {_, _, newNode -> Tooltip.install(newNode, Tooltip("${series.name} - $formattedTime"))}
            }

            data.add(series)
        }
    }

    companion object {
        private const val HEIGHT_PER_Y_AXIS_ELEMENT = 40
        private const val HEIGHT_PER_X_AXIS_ELEMENT = 35
        private const val ADDITIONAL_HEIGHT = 150
        private val LOGGER = LoggerFactory.getLogger(TimePerProjectAndUserGraphStatistics::class.java)
    }
}