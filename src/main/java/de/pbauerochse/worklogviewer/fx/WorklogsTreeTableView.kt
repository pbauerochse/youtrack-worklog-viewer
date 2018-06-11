package de.pbauerochse.worklogviewer.fx

import javafx.scene.control.TreeTableView

/**
 * Displays the [Issue]s in a TreeTableView
 */
class WorklogsTreeTableView : TreeTableView<Any>() {

    init {
        isShowRoot = false
    }


}