package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import javafx.scene.control.TreeItem

/**
 * Converter for building the [ReportView] to a list of [TreeItem]s
 */
internal object TreeItemConverter {

    /**
     * Converts the rows of the ReportView to [TreeItem]s
     * to be used in the [de.pbauerochse.worklogviewer.fx.components.treetable.TimeReportTreeTableView]
     */
    internal fun convert(reportView: ReportView): TreeItem<ReportRow> {
        val root: TreeItem<ReportRow> = TreeItem()
        convertAndAdd(root, reportView.rows)
        return root
    }

    private fun convertAndAdd(parent: TreeItem<ReportRow>, rows: List<ReportRow>) {
        parent.isExpanded = rows.isNotEmpty()

        rows.asSequence()
            .map {
                val treeItem = TreeItem(it)
                convertAndAdd(treeItem, it.children)
                return@map treeItem
            }
            .forEach { parent.children.add(it) }
    }
}
