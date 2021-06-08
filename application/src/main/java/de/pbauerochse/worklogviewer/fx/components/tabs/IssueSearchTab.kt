package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Tab

/**
 * Tab-View that allows searching for issues
 */
class IssueSearchTab : Tab(getFormatted("search.title")) {
    init {
        val loader = FXMLLoader(IssueSearchTab::class.java.getResource("/fx/views/search-issues.fxml"), FormattingUtil.RESOURCE_BUNDLE)
        val view = loader.load<Parent>()
        content = view
    }
}