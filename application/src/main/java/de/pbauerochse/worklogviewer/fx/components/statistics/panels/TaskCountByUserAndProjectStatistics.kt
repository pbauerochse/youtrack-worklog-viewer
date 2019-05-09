package de.pbauerochse.worklogviewer.fx.components.statistics.panels

import de.pbauerochse.worklogviewer.fx.components.statistics.data.ProjectSummary
import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.data.UserStatistic
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

            it.projectSummaries.forEach {
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
        setConstraints(label, 0, index)
        setColumnSpan(label, 3)
        return label
    }

    private fun getPercentageLabel(currentRow: Int, projectSummary: ProjectSummary): Label {
        val percentageFormatted = formatPercentage(projectSummary.percentage)
        val percentageLabel = Label(percentageFormatted)
        percentageLabel.styleClass.add("task-by-user-statistics-percentage")
        setHalignment(percentageLabel, HPos.RIGHT)
        setConstraints(percentageLabel, 1, currentRow)
        return percentageLabel
    }

    private fun getProjectLabel(currentRow: Int, it: ProjectSummary): Label {
        val projectLabel = Label(getFormatted("view.statistics.somethingtoamountoftickets", it.projectId, it.numberOfIssues))
        projectLabel.styleClass.add("task-by-user-statistics-project")
        setConstraints(projectLabel, 2, currentRow)
        return projectLabel
    }

    private fun getProjectSpentTimeLabel(currentRow: Int, it: ProjectSummary): Label {
        val timespentLabel = Label(formatMinutes(it.timeSpentInMinutes, true))
        timespentLabel.styleClass.add("task-by-user-statistics-project-spent-time")
        setConstraints(timespentLabel, 3, currentRow)
        setHalignment(timespentLabel, HPos.RIGHT)
        setHgrow(timespentLabel, Priority.ALWAYS)
        timespentLabel.applyCss()
        return timespentLabel
    }

    private fun getTotalSpentTimeLabel(currentRow: Int): Label {
        val label = Label(getFormatted("view.statistics.totaltimespent"))
        label.styleClass.add("task-by-user-statistics-total-spent-time-label")
        setConstraints(label, 0, currentRow)
        setColumnSpan(label, 2)
        setHalignment(label, HPos.RIGHT)
        return label
    }

    private fun getTotalSpentTime(currentRow: Int, data: UserStatistic): Label {
        val label = Label(formatMinutes(data.totalTimeSpent, true))
        label.styleClass.add("task-by-user-statistics-total-spent-time")
        setConstraints(label, 3, currentRow)
        setHalignment(label, HPos.RIGHT)
        setHgrow(label, Priority.ALWAYS)
        return label
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TaskCountByUserAndProjectStatistics::class.java)
    }
}

