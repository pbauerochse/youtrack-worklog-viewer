package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.StatisticsPane
import de.pbauerochse.worklogviewer.fx.components.treetable.WorklogsTreeTableView
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.youtrack.TimeReport
import de.pbauerochse.worklogviewer.youtrack.TimeReportParameters
import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import org.slf4j.LoggerFactory

/**
 * Abstract class to display parts of the result
 * of a [TimeReport]
 */
abstract class WorklogsTab(label: String) : Tab(label) {

    private val worklogsTableView = WorklogsTreeTableView()
    private val statisticsPane = StatisticsPane()
    private val splitPane = SplitPane(getWorklogsTableView()).apply { orientation = Orientation.HORIZONTAL }
    private val settingsModel = SettingsUtil.settingsViewModel

    private var nextIssues: List<Issue>? = null
    private var nextReportParameters: TimeReportParameters? = null

    init {
        content = splitPane
        selectedProperty().addListener { _, _, _ -> renderContent() }
        settingsModel.showStatisticsProperty().addListener { _, _, showStatistics -> showStatisticsView(showStatistics) }
        showStatisticsView(settingsModel.isShowStatistics)
    }

    fun update(label: String, reportParameters: TimeReportParameters, issues: List<Issue>) {
        text = label
        nextIssues = issues
        nextReportParameters = reportParameters

        if (isSelected) {
            renderContent()
        }
    }

    /**
     * Returns a list of component nodes, that are
     * supposed to be shown in the statistics panel
     */
    internal abstract fun getStatistics(issues: List<Issue>): List<Node>

    private fun showStatisticsView(showStatistics: Boolean) {
        if (showStatistics && !splitPane.items.contains(statisticsPane)) {
            LOGGER.debug("Showing '$text' with statistics panel")
            splitPane.items.add(statisticsPane)
            splitPane.setDividerPosition(0, 0.8)
        } else if (!showStatistics && splitPane.items.contains(statisticsPane)) {
            LOGGER.debug("Showing '$text' without statistics")
            splitPane.items.remove(statisticsPane)
        }
    }

    private fun renderContent() {
        if (nextIssues != null) {
            LOGGER.debug("Rendering ${nextIssues!!.size} Issues")
            worklogsTableView.setIssues(nextIssues!!, nextReportParameters!!)

            val statistics = getStatistics(nextIssues!!)
            statisticsPane.replaceAll(statistics)

            nextIssues = null
        }
    }

    private fun getWorklogsTableView(): Node {
        val anchorPane = AnchorPane(worklogsTableView).apply {
            padding = Insets(6.0)
        }

        AnchorPane.setTopAnchor(worklogsTableView, 0.0)
        AnchorPane.setRightAnchor(worklogsTableView, 0.0)
        AnchorPane.setBottomAnchor(worklogsTableView, 0.0)
        AnchorPane.setLeftAnchor(worklogsTableView, 0.0)

        return anchorPane
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorklogsTab::class.java)
    }


}