package de.pbauerochse.worklogviewer.search.fx.results

import de.pbauerochse.worklogviewer.tag.fx.IssueTagLabel
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.fx.table.columns.context.IssueCellContextMenu
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import java.time.LocalDate

/**
 * Custom component to display more information about
 * an [Issue] in a [javafx.scene.control.ListView]
 */
class SearchResultListViewItem : ListCell<Issue>() {

    lateinit var listViewItem: Pane
    lateinit var issueIdLabel: Label
    lateinit var issueTitleLabel: Label
    lateinit var issueDescriptionLabel: Label
    lateinit var fieldsFlowPane: FlowPane
    lateinit var tagsFlowPane: FlowPane

    init {
        val loader = FXMLLoader(SearchResultListViewItem::class.java.getResource("/fx/views/search-result-listview-item.fxml"))
        loader.setController(this)
        loader.load<Parent>()

        padding = Insets(5.0)
    }

    override fun updateItem(issue: Issue?, empty: Boolean) {
        super.updateItem(issue, empty)
        text = null
        listViewItem.styleClass.remove(RESOLVED_CLASS)

        if (issue == null || empty) {
            graphic = null
            fieldsFlowPane.children.clear()
        } else {
            issueIdLabel.text = issue.humanReadableId
            issueTitleLabel.text = issue.title
            issueDescriptionLabel.text = issue.descriptionPlaintext
            fieldsFlowPane.children.setAll(
                issue.fields
                    .filter { it.value.isNotEmpty() }
                    .map { SearchResultIssueField(it) }
            )
            tagsFlowPane.children.setAll(issue.tags.map { IssueTagLabel(it) })

            if (issue.isResolved) {
                listViewItem.styleClass.add(RESOLVED_CLASS)
            }

            contextMenu = IssueCellContextMenu(issue, date = LocalDate.now(), showAddForOtherIssueItem = false)
            graphic = listViewItem
        }
    }

    companion object {
        private const val RESOLVED_CLASS = "resolved"
    }
}