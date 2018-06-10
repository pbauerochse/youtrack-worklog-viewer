package de.pbauerochse.worklogviewer.fx.tabs

import de.pbauerochse.worklogviewer.youtrack.TimeReport
import de.pbauerochse.worklogviewer.youtrack.domain.Project
import javafx.scene.control.Tab

/**
 * Abstract class to display parts of the result
 * of a [TimeReport]
 */
abstract class WorklogsTab(label : String) : Tab(label) {

    init {
//        content = createContentNode()
    }

    fun update(project: Project) {
        text = project.id

    }

//    private fun createContentNode() : Node {
//
//    }

}