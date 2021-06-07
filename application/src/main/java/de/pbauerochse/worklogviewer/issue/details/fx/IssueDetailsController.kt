package de.pbauerochse.worklogviewer.issue.details.fx

import de.pbauerochse.worklogviewer.fx.Theme
import de.pbauerochse.worklogviewer.fx.issuesearch.WebViewSanitizer
import de.pbauerochse.worklogviewer.search.fx.SearchTabModel
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.timereport.WorkItem
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.scene.web.WebView
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

/**
 * FX Controller for displaying the details of an [de.pbauerochse.worklogviewer.timereport.Issue]
 */
class IssueDetailsController : Initializable {

    lateinit var issueDetailsPanel: BorderPane
    lateinit var placeholderContent: Label

    lateinit var issueSummaryLabel: Label
    lateinit var issueDescriptionWebView: WebView
    lateinit var issueWorklogsTableView: TableView<WorkItem>

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        placeholderContent.visibleProperty().bind(SearchTabModel.selectedIssueForDetails.isNull)
        issueDetailsPanel.visibleProperty().bind(SearchTabModel.selectedIssueForDetails.isNotNull)

        issueSummaryLabel.textProperty().bind(Bindings.createStringBinding({ SearchTabModel.selectedIssueForDetails.get()?.fullTitle }, SearchTabModel.selectedIssueForDetails))
        SearchTabModel.selectedIssueForDetails.addListener { _, _, newValue ->
            LOGGER.info("Showing Issue Details for $newValue")
            issueDescriptionWebView.engine.loadContent(WebViewSanitizer.sanitize(newValue.description))
        }
        issueWorklogsTableView.itemsProperty().bind(SimpleObjectProperty(SearchTabModel.selectedIssueWorkItems))
        SettingsUtil.settingsViewModel.themeProperty.addListener { _, _, newValue ->
            updateWebviewStyleSheet(newValue)
        }
        updateWebviewStyleSheet(SettingsUtil.settings.theme)
    }

    private fun updateWebviewStyleSheet(theme: Theme) {
        issueDescriptionWebView.engine.userStyleSheetLocation = IssueDetailsController::class.java.getResource(theme.webviewStylesheet)?.toExternalForm()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueDetailsController::class.java)
    }
}