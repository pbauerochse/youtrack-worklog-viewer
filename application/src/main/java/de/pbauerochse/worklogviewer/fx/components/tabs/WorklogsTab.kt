package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.StatisticsPane
import de.pbauerochse.worklogviewer.fx.dialog.Dialog
import de.pbauerochse.worklogviewer.fx.tasks.ExportToExcelTask
import de.pbauerochse.worklogviewer.fx.tasks.TaskExecutor
import de.pbauerochse.worklogviewer.plugins.dialog.FileChooserSpecification
import de.pbauerochse.worklogviewer.plugins.dialog.FileType
import de.pbauerochse.worklogviewer.plugins.state.TabContext
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.TimeReportParameters
import de.pbauerochse.worklogviewer.timereport.fx.table.TimeReportTreeTableView
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.ReportViewFactory
import de.pbauerochse.worklogviewer.view.grouping.Grouping
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import org.slf4j.LoggerFactory

/**
 * Abstract class to display parts of the result
 * of a [de.pbauerochse.worklogviewer.timereport.TimeReport]
 */
abstract class WorklogsTab(label: String) : Tab(label), TabContext {

    private val worklogsTableView = TimeReportTreeTableView()
    private val statisticsPane = StatisticsPane()
    private val splitPane = SplitPane(getWorklogsTableView()).apply { orientation = Orientation.HORIZONTAL }
    private val settingsModel = SettingsUtil.settingsViewModel

    private var currentData: ReportView? = null
    private var nextData: ReportView? = null

    init {
        content = splitPane
        selectedProperty().addListener { _, _, _ -> renderContent() }
        settingsModel.showStatisticsProperty.addListener { _, _, showStatistics -> showStatisticsView(showStatistics) }
        showStatisticsView(settingsModel.showStatisticsProperty.get())
    }

    fun update(label: String, filteredIssues: List<IssueWithWorkItems>, reportParameters: TimeReportParameters, grouping: Grouping) {
        text = label
        nextData = ReportViewFactory.convert(filteredIssues, reportParameters, grouping)

        if (isSelected) {
            renderContent()
        }
    }

    /**
     * Returns a list of component nodes, that are
     * supposed to be shown in the statistics panel
     */
    internal abstract fun getStatistics(reportView: ReportView): List<Node>

    private fun showStatisticsView(showStatistics: Boolean) {
        if (showStatistics && !splitPane.items.contains(statisticsPane)) {
            splitPane.items.add(statisticsPane)
            splitPane.setDividerPosition(0, 0.78)
            splitPane.dividers[0].positionProperty().bindBidirectional(settingsModel.statisticsPaneDividerPosition)
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

            val statistics = getStatistics(currentData!!)
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

    fun startDownloadAsExcel(executor: TaskExecutor) {
        // ask the user where to save the target file
        val timerange = currentData!!.reportParameters.timerange

        Dialog(content.scene)
            .showSaveFileDialog(FileChooserSpecification(getFormatted("view.menu.file.exportexcel"), "${text}_$timerange.xls", FileType("Microsoft Excel", "*.xls"))) {
                LOGGER.debug("Exporting tab {} to excel {}", text, it.absoluteFile)
                executor.startTask(ExportToExcelTask(text, currentData!!, it))
            }
    }

    override val view: ReportView
        get() = this.currentData!!

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorklogsTab::class.java)
    }


}