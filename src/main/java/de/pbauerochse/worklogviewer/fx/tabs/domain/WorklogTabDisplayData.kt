package de.pbauerochse.worklogviewer.fx.tabs.domain

import javafx.scene.control.TreeItem

/**
 * @author Patrick Bauerochse
 * @since 09.07.15
 */
class WorklogTabDisplayData(
    private val treeRows: MutableList<TreeItem<DisplayRow>> = mutableListOf()
) {

    fun getTreeRows(): List<TreeItem<DisplayRow>> {
        return treeRows
    }

    fun addRow(rowTreeItem: TreeItem<DisplayRow>) {
        treeRows.add(rowTreeItem)
    }

}
