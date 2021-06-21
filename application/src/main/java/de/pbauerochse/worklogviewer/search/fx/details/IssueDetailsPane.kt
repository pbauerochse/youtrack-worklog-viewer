package de.pbauerochse.worklogviewer.search.fx.details

import de.pbauerochse.worklogviewer.fx.Theme
import de.pbauerochse.worklogviewer.fx.issuesearch.WebViewSanitizer
import de.pbauerochse.worklogviewer.search.fx.results.SearchResultIssueField
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.tasks.Tasks
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.WorkItem
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.web.WebView
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

class IssueDetailsPane(private val issue: Issue): BorderPane(), Initializable {

    lateinit var issueSummaryLabel: Label
    lateinit var issueFieldsPane: FlowPane
    lateinit var issueDescriptionWebView: WebView
    lateinit var issueWorklogsTableView: TableView<WorkItem>

    private val workItems: ObservableList<WorkItem> = FXCollections.observableArrayList()

    init {
        FXMLLoader(IssueDetailsPane::class.java.getResource("/fx/views/issue-details.fxml")).apply {
            setController(this@IssueDetailsPane)
            setRoot(this@IssueDetailsPane)
        }.load<BorderPane>()
    }

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        LOGGER.info("Preparing details pane for $issue")
        issueSummaryLabel.text = issue.fullTitle
        issueDescriptionWebView.engine.loadContent(WebViewSanitizer.sanitize(issue.description))
        issueWorklogsTableView.itemsProperty().bind(SimpleObjectProperty(workItems))
        issue.fields.forEach { issueFieldsPane.children.add(SearchResultIssueField(it)) }

        SettingsUtil.settingsViewModel.themeProperty.addListener { _, _, newValue -> updateWebviewStyleSheet(newValue) }

        updateWebviewStyleSheet(SettingsUtil.settings.theme)
        loadIssueWorkItems()
    }

    private fun loadIssueWorkItems() {
        val task = FetchWorkItemsForIssueTask(issue).apply {
            onSucceeded = EventHandler { workItems.setAll(this.value.workItems) }
        }
        Tasks.startBackgroundTask(task)
    }

    private fun updateWebviewStyleSheet(theme: Theme) {
        issueDescriptionWebView.engine.userStyleSheetLocation = IssueDetailsPane::class.java.getResource(theme.webviewStylesheet)?.toExternalForm()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueDetailsPane::class.java)
    }
}