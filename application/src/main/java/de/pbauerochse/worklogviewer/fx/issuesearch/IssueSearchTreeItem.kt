package de.pbauerochse.worklogviewer.fx.issuesearch

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
            return IssueSearchTreeItem(label, {})
        }
    }
}