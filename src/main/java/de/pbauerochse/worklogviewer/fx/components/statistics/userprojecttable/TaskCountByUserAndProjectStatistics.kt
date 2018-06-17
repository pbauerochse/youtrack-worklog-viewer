package de.pbauerochse.worklogviewer.fx.components.statistics.userprojecttable

import de.pbauerochse.worklogviewer.fx.components.statistics.data.ProjectStatistic
import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.data.UserStatistic
import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.*
import javafx.geometry.HPos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import org.slf4j.LoggerFactory

/**
 * Shows a table for each user containing an
 * overview of the amount of tasks and spent
 * time in each project
 */
class TaskCountByUserAndProjectStatistics(private val data: TaskCountByUserAndProjectStatisticData) : GridPane() {

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

        data.userStatistics.forEach {
            val label = getUserDisplayNameLabel(currentRow++, it)
            children.add(label)

            it.projectStatistics.forEach {
                val percentage = getPercentageLabel(currentRow, it)
                val projectAndNumberOfTickets = getProjectLabel(currentRow, it)
                val projectTotalSpentTime = getProjectSpentTimeLabel(currentRow, it)
                children.addAll(percentage, projectAndNumberOfTickets, projectTotalSpentTime)
                currentRow++
            }

            children.addAll(
                getTotalSpentTimeLabel(currentRow),
                getTotalSpentTime(currentRow, it)
            )

            // spacing to next user section
            currentRow += 2
        }
    }

    private fun getUserDisplayNameLabel(index: Int, userStatistic: UserStatistic): Label {
        val totalNumberOfTickets = userStatistic.totalNumberOfTickets
        val label = Label(getFormatted("view.statistics.somethingtoamountoftickets", userStatistic.userDisplayLabel, totalNumberOfTickets))
        label.styleClass.add("task-by-user-statistics-username")
//        label.padding = Insets(20.0, 0.0, 0.0, 0.0)
        GridPane.setConstraints(label, 0, index)
        GridPane.setColumnSpan(label, 3)
        return label
    }

    private fun getPercentageLabel(currentRow: Int, projectStatistic: ProjectStatistic): Label {
        val percentageFormatted = formatPercentage(projectStatistic.percentage)
        val percentageLabel = Label(percentageFormatted)
        percentageLabel.styleClass.add("task-by-user-statistics-percentage")
        GridPane.setHalignment(percentageLabel, HPos.RIGHT)
        GridPane.setConstraints(percentageLabel, 1, currentRow)
        return percentageLabel
    }

    private fun getProjectLabel(currentRow: Int, it: ProjectStatistic): Label {
        val projectLabel = Label(getFormatted("view.statistics.somethingtoamountoftickets", it.projectId, it.numberOfIssues))
        projectLabel.styleClass.add("task-by-user-statistics-project")
        GridPane.setConstraints(projectLabel, 2, currentRow)
        return projectLabel
    }

    private fun getProjectSpentTimeLabel(currentRow: Int, it: ProjectStatistic): Label {
        val timespentLabel = Label(formatMinutes(it.timeSpentInMinutes, true))
        timespentLabel.styleClass.add("task-by-user-statistics-project-spent-time")
        GridPane.setConstraints(timespentLabel, 3, currentRow)
        GridPane.setHalignment(timespentLabel, HPos.RIGHT)
        GridPane.setHgrow(timespentLabel, Priority.ALWAYS)
        timespentLabel.applyCss()
        return timespentLabel
    }

    private fun getTotalSpentTimeLabel(currentRow: Int): Label {
        val label = Label(getFormatted("view.statistics.totaltimespent"))
        label.styleClass.add("task-by-user-statistics-total-spent-time-label")
        GridPane.setConstraints(label, 0, currentRow)
        GridPane.setColumnSpan(label, 2)
        GridPane.setHalignment(label, HPos.RIGHT)
        return label
    }

    private fun getTotalSpentTime(currentRow: Int, data: UserStatistic): Label {
        val label = Label(FormattingUtil.formatMinutes(data.totalTimeSpent, true))
        label.styleClass.add("task-by-user-statistics-total-spent-time")
        GridPane.setConstraints(label, 3, currentRow)
        GridPane.setHalignment(label, HPos.RIGHT)
        GridPane.setHgrow(label, Priority.ALWAYS)
        return label
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TaskCountByUserAndProjectStatistics::class.java)
    }
}

