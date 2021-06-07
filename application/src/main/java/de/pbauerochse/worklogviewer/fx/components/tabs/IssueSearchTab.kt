package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.tasks.TaskExecutor
import de.pbauerochse.worklogviewer.search.fx.SearchTabController
import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Tab

/**
 * Tab-View that allows searching for issues
 */
class IssueSearchTab(taskExecutor: TaskExecutor) : Tab(getFormatted("search.title")) {
    init {
        val loader = FXMLLoader(IssueSearchTab::class.java.getResource("/fx/views/search-issues.fxml"), FormattingUtil.RESOURCE_BUNDLE)
        val view = loader.load<Parent>()
        val controller = loader.getController<SearchTabController>()
        controller.taskExecutor = taskExecutor
        content = view
    }
}