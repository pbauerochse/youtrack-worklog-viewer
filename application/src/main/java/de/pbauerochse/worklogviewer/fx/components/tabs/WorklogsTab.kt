package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.StatisticsPane
import de.pbauerochse.worklogviewer.fx.components.treetable.WorklogsTreeTableView
import de.pbauerochse.worklogviewer.fx.components.treetable.WorklogsTreeTableViewData
import de.pbauerochse.worklogviewer.fx.tasks.ExportToExcelTask
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import javafx.stage.FileChooser
import org.slf4j.LoggerFactory

/**
 * Abstract class to display parts of the result
 * of a [de.pbauerochse.worklogviewer.report.TimeReport]
 */
abstract class WorklogsTab(label: String) : Tab(label) {

    private val worklogsTableView = WorklogsTreeTableView()
    private val statisticsPane = StatisticsPane()
    private val splitPane = SplitPane(getWorklogsTableView()).apply { orientation = Orientation.HORIZONTAL }
    private val settingsModel = SettingsUtil.settingsViewModel

    var currentData : WorklogsTreeTableViewData? = null
    private var nextData : WorklogsTreeTableViewData? = null

    init {
        content = splitPane
        selectedProperty().addListener { _, _, _ -> renderContent() }
        settingsModel.showStatisticsProperty.addListener { _, _, showStatistics -> showStatisticsView(showStatistics) }
        showStatisticsView(settingsModel.showStatisticsProperty.get())
    }

    fun update(label: String, reportParameters: TimeReportParameters, issues: List<Issue>) {
        text = label
        nextData = WorklogsTreeTableViewData(reportParameters, issues)

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
            splitPane.items.add(statisticsPane)
            splitPane.setDividerPosition(0, 0.8)
        } else if (!showStatistics && splitPane.items.contains(statisticsPane)) {
            splitPane.items.remove(statisticsPane)
        }
    }

    private fun renderContent() {
        if (nextData != null) {
            LOGGER.debug("Rendering ${nextData!!.issues.size} Issues")
            currentData = nextData
            nextData = null

            worklogsTableView.update(currentData!!)

            val statistics = getStatistics(currentData!!.issues)
            statisticsPane.replaceAll(statistics)
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

    fun getDownloadAsExcelTask(): ExportToExcelTask? {
        // ask the user where to save the target file
        val timerange = currentData!!.reportParameters.timerange

        val fileChooser = FileChooser()
        fileChooser.title = FormattingUtil.getFormatted("view.menu.file.exportexcel")
        fileChooser.initialFileName = "${text}_$timerange.xls"
        fileChooser.selectedExtensionFilter = FileChooser.ExtensionFilter("Microsoft Excel", "*.xls")

        val targetFile = fileChooser.showSaveDialog(content.scene.window)
        return targetFile?.let {
            LOGGER.debug("Exporting tab {} to excel {}", text, it.absoluteFile)
            return ExportToExcelTask(text, currentData!!, it)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorklogsTab::class.java)
    }


}