package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.treetable.WorklogsTreeTableView
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.youtrack.TimeReport
import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import org.slf4j.LoggerFactory

/**
 * Abstract class to display parts of the result
 * of a [TimeReport]
 */
abstract class WorklogsTab(label: String) : Tab(label) {

    private val worklogsTableView = WorklogsTreeTableView()
    private val settingsModel = SettingsUtil.settingsViewModel

    private var nextIssues: List<Issue>? = null

    init {
        content = createContentNode()
        selectedProperty().addListener({_,_,_ -> render()})
    }

    fun update(label: String, issues: List<Issue>) {
        text = label
        nextIssues = issues
    }

    private fun render() {
        if (nextIssues != null) {
            LOGGER.debug("Rendering ${nextIssues!!.size} Issues")
            worklogsTableView.setIssues(nextIssues!!)
            nextIssues = null
        }
    }

    private fun createContentNode(): Node {
        return if (settingsModel.isShowStatistics) {
            LOGGER.debug("Showing '$text' with statistics panel")
            getWithStatisticsPanel()
        } else {
            LOGGER.debug("Showing '$text' without statistics")
            worklogsTableView
        }
    }

    private fun getWithStatisticsPanel(): Node {
        val statisticsView = getStatisticsView()
        val worklogsView = getWorklogsTableView()

        return SplitPane(worklogsView, statisticsView).apply {
            orientation = Orientation.HORIZONTAL
            setDividerPosition(0, 0.8)
        }
    }

    private fun getStatisticsView(): Node {
        val statistics = VBox(20.0)
        return ScrollPane(statistics).apply {
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            isFitToWidth = true
            padding = Insets(7.0)
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