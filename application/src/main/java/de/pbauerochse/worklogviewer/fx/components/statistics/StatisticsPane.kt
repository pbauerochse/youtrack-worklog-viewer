package de.pbauerochse.worklogviewer.fx.components.statistics

import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox

class StatisticsPane : ScrollPane() {

    private val statisticsContainer = VBox(20.0)

    init {
        statisticsContainer.styleClass.add("statistics-pane")
        hbarPolicy = ScrollBarPolicy.NEVER
        isFitToWidth = true
        content = statisticsContainer
    }

    /**
     * Replaces all currently shown statistics
     * with passed in components
     */
    fun replaceAll(statistics: List<Node>) {
        statisticsContainer.children.clear()
        statisticsContainer.children.addAll(statistics)
    }
}