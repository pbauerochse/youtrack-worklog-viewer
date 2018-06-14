package de.pbauerochse.worklogviewer.fx.components.statistics

import de.pbauerochse.worklogviewer.util.FormattingUtil.*
import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import org.slf4j.LoggerFactory

/**
 *
 */
class TaskCountByUserAndProjectStatistics(private val issues: List<Issue>) : GridPane() {

    private var alreadyRendered = false

    init {
        hgap = 5.0
        vgap = 5.0

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
        var currentRow = 0
        val data = TaskCountByUserAndProjectStatisticData(issues)

        data.userStatistics.forEach {
            val label = getUserDisplayNameLabel(currentRow++, it)
            children.add(label)

            it.projectStatistics.forEach {
                val percentage = getPercentageLabel(currentRow, it)
                val projectAndNumberOfTickets = getProjectLabel(currentRow, it)
                val projectTotalSpentTime = getTotalSpenttimeLabel(currentRow, it)
                children.addAll(percentage, projectAndNumberOfTickets, projectTotalSpentTime)
                currentRow++
            }
            // TOTAL
            // SPACER
        }
    }

    private fun getUserDisplayNameLabel(index: Int, userStatistic: UserStatistic): Label {
        val totalNumberOfTickets = userStatistic.totalNumberOfTickets
        val label = Label(getFormatted("view.statistics.somethingtoamountoftickets", userStatistic, totalNumberOfTickets))
        label.styleClass.add("task-by-user-statistics-username")
//        label.padding = Insets(20.0, 0.0, 0.0, 0.0)
        GridPane.setConstraints(label, 0, index)
        GridPane.setColumnSpan(label, 4)
        return label
    }

    private fun getPercentageLabel(currentRow: Int, projectStatistic: ProjectStatistic): Label {
        val percentageFormatted = formatPercentage(projectStatistic.percentage)
        val percentageLabel = Label(percentageFormatted)
        percentageLabel.styleClass.add("task-by-user-statistics-percentage")
//        percentageLabel.setAlignment(Pos.CENTER_RIGHT)
//        percentageLabel.setPadding(Insets(0.0, 0.0, 0.0, 20.0))
//        GridPane.setHalignment(percentageLabel, HPos.RIGHT)
        GridPane.setConstraints(percentageLabel, 1, currentRow)
        return percentageLabel
    }

    private fun getProjectLabel(currentRow: Int, it: ProjectStatistic): Label {
        val projectLabel = Label(getFormatted("view.statistics.somethingtoamountoftickets", it.id, it.numberOfIssues))
        projectLabel.styleClass.add("task-by-user-statistics-project")
        GridPane.setConstraints(projectLabel, 2, currentRow)
        return projectLabel
    }

    private fun getTotalSpenttimeLabel(currentRow: Int, it: ProjectStatistic): Label {
        val timespentLabel = Label(formatMinutes(it.timespentInMinutes, true))
        GridPane.setConstraints(timespentLabel, 3, currentRow)
        timespentLabel.styleClass.add("task-by-user-statistics-timespent")
//        GridPane.setHgrow(timespentLabel, Priority.ALWAYS)
//        GridPane.setHalignment(timespentLabel, HPos.RIGHT)
        return timespentLabel
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TaskCountByUserAndProjectStatistics::class.java)
    }
}

