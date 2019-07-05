package de.pbauerochse.worklogviewer.fx.issuesearch.treeview

import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses
import de.pbauerochse.worklogviewer.fx.issuesearch.IssueSearchTreeItem
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback

class IssueSearchTreeColumn : TreeTableColumn<IssueSearchTreeItem, IssueSearchTreeItem>() {
    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(col.value.value) }
        cellFactory = Callback { IssueSearchResultCell() }
        prefWidth = 300.0
        minWidth = 300.0
    }
}

private class IssueSearchResultCell : TreeTableCell<IssueSearchTreeItem, IssueSearchTreeItem>() {

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

}