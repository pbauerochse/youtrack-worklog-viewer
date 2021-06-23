package de.pbauerochse.worklogviewer.details

import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.events.Subscribe
import de.pbauerochse.worklogviewer.fx.Theme
import de.pbauerochse.worklogviewer.fx.issuesearch.WebViewSanitizer
import de.pbauerochse.worklogviewer.search.fx.results.SearchResultIssueField
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.tasks.Tasks
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.Tag
import de.pbauerochse.worklogviewer.timereport.WorkItem
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.workitem.add.event.WorkItemAddedEvent
import de.pbauerochse.worklogviewer.workitem.add.fx.AddWorkItemDialog
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.web.WebView
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDate
import java.util.*

class IssueDetailsPane(private val issue: Issue) : BorderPane(), Initializable {

    lateinit var issueSummaryLabel: Label
    lateinit var issueTagsPane: FlowPane
    lateinit var issueFieldsPane: FlowPane
    lateinit var issueDescriptionWebView: WebView
    lateinit var issueWorklogsTableView: TableView<WorkItem>
    lateinit var createWorkItemButton: Button

    private val workItems: ObservableList<WorkItem> = FXCollections.observableArrayList()

    init {
        FXMLLoader(IssueDetailsPane::class.java.getResource("/fx/views/issue-details.fxml")).apply {
            setController(this@IssueDetailsPane)
            setRoot(this@IssueDetailsPane)
        }.load<BorderPane>()

        EventBus.subscribe(this)
    }

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        LOGGER.info("Preparing details pane for $issue")
        createWorkItemButton.apply {
            textProperty().set(getFormatted("contextmenu.issue.addworkitem", issue.humanReadableId))
            setOnAction { AddWorkItemDialog.show(scene, issue = issue, date = LocalDate.now()) }
        }
        issueSummaryLabel.text = issue.fullTitle
        issueDescriptionWebView.engine.loadContent(WebViewSanitizer.sanitize(issue.description))
        issueWorklogsTableView.apply {
            itemsProperty().bind(SimpleObjectProperty(workItems))
        }
        issue.fields.forEach { issueFieldsPane.children.add(SearchResultIssueField(it)) }
        issue.tags.forEach { issueTagsPane.children.add(createTagLabel(it)) }

        SettingsUtil.settingsViewModel.themeProperty.addListener { _, _, newValue -> updateWebviewStyleSheet(newValue) }

        updateWebviewStyleSheet(SettingsUtil.settings.theme)
        loadIssueWorkItems()
    }

    private fun createTagLabel(tag: Tag): Label {
        val styles = listOfNotNull(
            tag.backgroundColorHex?.let { "-fx-background-color: $it;" },
            tag.foregroundColorHex?.let { "-fx-fill: $it;" },
            tag.foregroundColorHex?.let { "-fx-text-fill: $it;" }
        )
        .joinToString(separator = "\n")

        return Label(tag.label).apply {
            style = styles
            styleClass.add("issue-tag")
        }
    }

    @Subscribe
    fun onWorkItemAddedEvent(event: WorkItemAddedEvent) {
        if (event.issue == issue) {
            LOGGER.debug("Adding new WorkItem to issue detail view for $issue")
            workItems.add(event.addedWorkItem)
            workItems.sortByDescending { it.workDate }
        }
    }

    private fun loadIssueWorkItems() {
        val task = FetchWorkItemsForIssueTask(issue).apply {
            onSucceeded = EventHandler {
                val sortedByDateDescending = this.value.workItems.sortedByDescending { it.workDateAtLocalZone }
                workItems.setAll(sortedByDateDescending)
            }
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