package de.pbauerochse.worklogviewer.details

import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.events.Subscribe
import de.pbauerochse.worklogviewer.fx.Theme
import de.pbauerochse.worklogviewer.fx.issuesearch.WebViewSanitizer
import de.pbauerochse.worklogviewer.search.fx.results.SearchResultIssueField
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.tag.fx.IssueTagLabel
import de.pbauerochse.worklogviewer.tasks.Tasks
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.WorkItem
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.util.WorklogTimeFormatter
import de.pbauerochse.worklogviewer.workitem.add.event.WorkItemAddedEvent
import de.pbauerochse.worklogviewer.workitem.add.fx.AddWorkItemDialog
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
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

    lateinit var progressStackPane: Pane
    lateinit var spentTimeProgressBar: ProgressBar
    lateinit var spentTimeLabel: Label

    private val workItems: ObservableList<WorkItem> = FXCollections.observableArrayList()

    private val totalSpentTimeInMinutesBinding = Bindings.createDoubleBinding({ workItems.sumOf { it.durationInMinutes }.toDouble() }, workItems)
    private val estimateInMinutesBinding = Bindings.selectDouble(SimpleObjectProperty(issue), "estimationInHours")
    private val spentTimePercentageBinding = Bindings.divide(totalSpentTimeInMinutesBinding, estimateInMinutesBinding)
    private val spentTimeTextProperty = SimpleStringProperty()
    private val estimateSetBinding = Bindings.greaterThan(estimateInMinutesBinding, 0.0)

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
        issueDescriptionWebView.engine.loadContent(WebViewSanitizer.sanitize(issue.descriptionWithHtmlMarkup))
        issueWorklogsTableView.apply {
            itemsProperty().bind(SimpleObjectProperty(workItems))
        }
        issue.fields.forEach { issueFieldsPane.children.add(SearchResultIssueField(it)) }
        issue.tags.forEach { issueTagsPane.children.add(IssueTagLabel(it)) }

        // estimation progressbar
        initializeSpentTimeProgressbar()

        SettingsUtil.settingsViewModel.themeProperty.addListener { _, _, newValue -> updateWebviewStyleSheet(newValue) }

        updateWebviewStyleSheet(SettingsUtil.settings.theme)
        loadIssueWorkItems()
    }

    private fun initializeSpentTimeProgressbar() {
        val formatter = WorklogTimeFormatter(SettingsUtil.settingsViewModel.workhoursProperty.value)

        with(progressStackPane) {
            managedProperty().bind(estimateSetBinding)
            visibleProperty().bind(estimateSetBinding)
        }

        spentTimeProgressBar.progressProperty().bind(spentTimePercentageBinding)

        with(spentTimeLabel) {
            textProperty().bind(spentTimeTextProperty)
            tooltip = Tooltip().apply {
                textProperty().bind(
                    Bindings.`when`(estimateSetBinding)
                        .then(getEstimateProgressbarTooltipText(formatter))
                        .otherwise("LEEEEEEEEER")
                )
            }
        }

        spentTimeTextProperty.bind(
            Bindings.createStringBinding(
                { "${formatter.getFormatted(totalSpentTimeInMinutesBinding.value.toLong())} / ${formatter.getFormatted(estimateInMinutesBinding.value.toLong())}" },
                totalSpentTimeInMinutesBinding, estimateInMinutesBinding
            )
        )
        spentTimePercentageBinding.addListener { _, _, newValue ->
            if (newValue.toDouble() > 1.0) {
                // spent time exceeds estimated time
                spentTimeProgressBar.styleClass.add("overbooked")
            }
        }
    }

    private fun getEstimateProgressbarTooltipText(formatter: WorklogTimeFormatter): StringBinding {
        return Bindings.createStringBinding(
            {
                val spentTime = totalSpentTimeInMinutesBinding.value!!.toLong()
                val estimate = estimateInMinutesBinding.value!!.toLong()
                val timeLeft = estimate - spentTime
                getFormatted("search.results.details.spenttime.tooltip", formatter.getFormatted(estimate), formatter.getFormatted(spentTime), formatter.getFormatted(timeLeft).takeIf { it.isNotBlank() } ?: "0")
            },
            totalSpentTimeInMinutesBinding, estimateInMinutesBinding
        )
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
                val sortedByDateDescending = value.workItems.sortedByDescending { it.workDateAtLocalZone }
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