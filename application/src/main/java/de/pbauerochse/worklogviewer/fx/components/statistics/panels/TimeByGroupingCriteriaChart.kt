package de.pbauerochse.worklogviewer.fx.components.statistics.panels

import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.util.WorklogTimeFormatter
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.chart.PieChart
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import java.text.NumberFormat

class TimeByGroupingCriteriaChart(reportView: ReportView) : StackPane() {

    init {
        styleClass.add("statistic-item")
        val mouseOverLabel = Label().apply {
            isPickOnBounds = false
            visibleProperty().set(false)
            styleClass.add("chart-label")
        }
        val pieChart = PieChart(generateData(reportView, mouseOverLabel)).apply {
            title = getFormatted("statistics.grouping.label", reportView.appliedGrouping!!.name)
            labelsVisible = false
        }

        children.addAll(pieChart, mouseOverLabel)
    }

    private fun generateData(reportView: ReportView, label: Label): ObservableList<PieChart.Data> {
        val data = reportView.rows
            .filter { it.isGrouping }
            .groupBy { it.label }
            .map {
                val dataPointValue = it.value.sumOf { reportRow -> reportRow.totalDurationInMinutes }
                createDataPoint(it.key, dataPointValue, label)
            }

        return FXCollections.observableArrayList(data)
    }

    private fun createDataPoint(name: String, value: Long, label: Label): PieChart.Data {
        val dataPoint = PieChart.Data(name, value.toDouble())
        attachMouseOverLabelListeners(dataPoint, label)
        return dataPoint
    }

    private fun attachMouseOverLabelListeners(dataPoint: PieChart.Data, label: Label) {
        dataPoint.nodeProperty().addListener { _, oldValue, newValue ->
            if (oldValue == null && newValue != null) {
                val percentage = calculatePercentage(dataPoint)

                newValue.onMouseExited = EventHandler { label.visibleProperty().set(false) }
                newValue.onMouseEntered = EventHandler { label.visibleProperty().set(true) }
                newValue.onMouseMoved = EventHandler {
                    label.translateX = it.x
                    label.translateY = it.y + 40.0
                    label.text = "${dataPoint.name}: ${WorklogTimeFormatter(SettingsUtil.settingsViewModel.workhoursProperty.value).getFormatted(dataPoint.pieValue.toLong())} (${PERCENTAGE_FORMATTER.format(percentage)})"
                }
            }
        }
    }

    private fun calculatePercentage(dataPoint: PieChart.Data): Double {
        val total = dataPoint.chart.data.sumOf { it.pieValue }
        val dataPointValue = dataPoint.pieValue
        return dataPointValue.div(total)
    }

    companion object {
        private val PERCENTAGE_FORMATTER = NumberFormat.getPercentInstance()
    }
}
