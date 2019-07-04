package de.pbauerochse.worklogviewer.fx.issuesearch.details

import de.pbauerochse.worklogviewer.fx.Theme
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.WorklogItem
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.scene.web.WebView
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

class IssueDetailsPanel : BorderPane(), Initializable {

    @FXML
    private lateinit var issueSummaryLabel: Label

    @FXML
    private lateinit var issueDescriptionWebView: WebView

    @FXML
    private lateinit var addWorklogButton: Button

    @FXML
    private lateinit var editWorklogButton: Button

    @FXML
    private lateinit var issueWorklogsTableView: TableView<WorklogItem>

    init {
        val loader = FXMLLoader(this::class.java.getResource("/fx/components/issue-details-panel.fxml"))
        loader.setRoot(this)
        loader.setController(this)
        loader.load<Parent>()
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val themeProperty = SettingsUtil.settingsViewModel.themeProperty
        themeProperty.addListener { _, _, newValue -> updateTheme(newValue) }
        updateTheme(themeProperty.value)

        issueWorklogsTableView.columns.addAll(
            WorklogDateColumn(),
            WorklogTypeColumn(),
            WorklogDescriptionColumn(),
            WorklogDurationColumn(),
            WorklogActionsColumn()
        )
    }

    fun update(issue: Issue) {
        LOGGER.info("Showing Issue Details for $issue")
        issueSummaryLabel.text = issue.fullTitle
        issueDescriptionWebView.engine.loadContent(issue.description)

        LOGGER.debug(issue.description)

        issueWorklogsTableView.items.setAll(issue.worklogItems)

        val dateColumn = issueWorklogsTableView.columns.find { it is WorklogDateColumn }!!
        dateColumn.sortType = TableColumn.SortType.DESCENDING
        issueWorklogsTableView.sortOrder.add(dateColumn)
    }

    private fun updateTheme(theme: Theme) {
        issueDescriptionWebView.engine.userStyleSheetLocation = IssueDetailsPanel::class.java.getResource(theme.webviewStylesheet).toExternalForm()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueDetailsPanel::class.java)
    }
}