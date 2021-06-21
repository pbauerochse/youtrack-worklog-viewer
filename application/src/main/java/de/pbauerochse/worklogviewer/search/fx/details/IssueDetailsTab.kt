package de.pbauerochse.worklogviewer.search.fx.details

import de.pbauerochse.worklogviewer.fx.Theme
import de.pbauerochse.worklogviewer.fx.issuesearch.WebViewSanitizer
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
import javafx.scene.control.Tab
import javafx.scene.control.TableView
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.web.WebView
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

/**
 * Contains the details of an [de.pbauerochse.worklogviewer.timereport.Issue]
 */
class IssueDetailsTab(private val issue: Issue) : Tab(issue.humanReadableId), Initializable {

    private val workItems: ObservableList<WorkItem> = FXCollections.observableArrayList()

    lateinit var issueDetailsPane: BorderPane
    lateinit var issueSummaryLabel: Label
    lateinit var issueDescriptionWebView: WebView
    lateinit var issueWorklogsTableView: TableView<WorkItem>

    init {
        isClosable = true
        tooltip = Tooltip(issue.fullTitle)

        val loader = FXMLLoader(IssueDetailsTab::class.java.getResource("/fx/views/issue-details.fxml"))
        loader.setController(this)
        loader.load<BorderPane>()
    }

    override fun initialize(url: URL?, resource: ResourceBundle?) {
        content = issueDetailsPane
        issueSummaryLabel.text = issue.fullTitle
        issueDescriptionWebView.engine.loadContent(WebViewSanitizer.sanitize(issue.description))
        issueWorklogsTableView.itemsProperty().bind(SimpleObjectProperty(workItems))
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
        issueDescriptionWebView.engine.userStyleSheetLocation = IssueDetailsTab::class.java.getResource(theme.webviewStylesheet)?.toExternalForm()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueDetailsTab::class.java)
    }
}