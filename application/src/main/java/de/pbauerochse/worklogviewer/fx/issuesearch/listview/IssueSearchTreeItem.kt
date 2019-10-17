package de.pbauerochse.worklogviewer.fx.issuesearch.listview

import de.pbauerochse.worklogviewer.fx.components.ComponentStyleClasses
import javafx.scene.control.ContextMenu

data class IssueSearchTreeItem(
    val label: String,
    val onSelect: () -> Unit,
    val contextMenu : ContextMenu? = null,
    val styleClasses : Collection<String> = emptySet()
) {

    override fun toString(): String = label

    companion object {
        fun labelledNoopItem(label: String): IssueSearchTreeItem {
            return IssueSearchTreeItem(label, {}, null, listOf(ComponentStyleClasses.TREE_GROUP_PARENT))
        }
    }
}