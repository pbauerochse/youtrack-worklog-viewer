package de.pbauerochse.worklogviewer.fx.issuesearch.listview

import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses
import de.pbauerochse.worklogviewer.fx.issuesearch.IssueSearchTreeItem
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeView
import javafx.util.Callback

class IssueSearchResultCell : TreeCell<IssueSearchTreeItem>() {

    override fun updateItem(item: IssueSearchTreeItem?, empty: Boolean) {
        super.updateItem(item, empty)

        styleClass.removeAll(ComponentStyleClasses.ALL_WORKLOGVIEWER_CLASSES)
        tooltip = null
        text = null

        item?.let {
            text = it.label
            tooltip = Tooltip(it.label)
            contextMenu = it.contextMenu
            styleClass.addAll(it.styleClasses)
        }
    }

    companion object {
        fun cellFactory() : Callback<TreeView<IssueSearchTreeItem>, TreeCell<IssueSearchTreeItem>> = Callback { IssueSearchResultCell() }
    }

}