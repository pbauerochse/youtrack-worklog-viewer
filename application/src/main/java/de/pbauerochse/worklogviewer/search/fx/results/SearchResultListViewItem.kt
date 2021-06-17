package de.pbauerochse.worklogviewer.search.fx.results

import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.fx.table.columns.context.IssueCellContextMenu
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.ListCell
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.text.Text

/**
 * Custom component to display more information about
 * an [Issue] in a [javafx.scene.control.ListView]
 */
class SearchResultListViewItem : ListCell<Issue>() {

    lateinit var listViewItem: BorderPane
    lateinit var issueIdLabel: Text
    lateinit var issueTitleLabel: Text
    lateinit var fieldsFlowPane: FlowPane

    init {
        val loader = FXMLLoader(SearchResultListViewItem::class.java.getResource("/fx/views/search-result-listview-item.fxml"))
        loader.setController(this)
        loader.load<Parent>()
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
            fieldsFlowPane.children.setAll(
                issue.fields
                    .filter { it.value.isNotEmpty() }
                    .map { SearchResultIssueField(it) }
            )

            issue.resolutionDate?.let {
                listViewItem.styleClass.add(RESOLVED_CLASS)
            }

            contextMenu = IssueCellContextMenu(issue, showAddForOtherIssueItem = false)
            graphic = listViewItem
        }
    }

    companion object {
        private const val RESOLVED_CLASS = "resolved"
    }
}