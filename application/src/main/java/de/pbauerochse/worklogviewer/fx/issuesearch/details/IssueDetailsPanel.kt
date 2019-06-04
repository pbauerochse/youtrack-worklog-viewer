package de.pbauerochse.worklogviewer.fx.issuesearch.details

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.WorklogItem
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDate
import java.util.*

class IssueDetailsPanel : BorderPane(), Initializable {

    @FXML
    private lateinit var issueSummaryLabel: Label

    @FXML
    private lateinit var issueDescriptionLabel: Label

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
        val dateCell = TableColumn<WorklogItem, LocalDate?>("DATUM").apply { cellFactory = WorklogDateCell.CELL_FACTORY }
        val descriptionCell = TableColumn<WorklogItem, String?>("DESCRIPTION").apply { cellValueFactory = PropertyValueFactory<WorklogItem, String?>("description") }
//        val durationCell = TableColumn<WorklogItem, String?>("DURATION").apply { cellFactory = PropertyValueFactory<WorklogItem, String?>("description") }

        issueWorklogsTableView.columns.addAll(
            dateCell,
            // type
            descriptionCell
            // duration
            // action buttons
        )
    }

    fun update(issue: Issue) {
        LOGGER.info("Loading IssueDetails $issue")
        issueSummaryLabel.text = issue.fullTitle
        issueDescriptionLabel.text = issue.description
        issueWorklogsTableView.items.setAll(issue.worklogItems)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueDetailsPanel::class.java)
    }
}