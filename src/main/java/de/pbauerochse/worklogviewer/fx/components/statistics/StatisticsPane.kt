package de.pbauerochse.worklogviewer.fx.components.statistics

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox

class StatisticsPane : ScrollPane() {

    private val statisticsContainer = VBox(20.0)

    init {
        hbarPolicy = ScrollBarPolicy.NEVER
        isFitToWidth = true
        padding = Insets(7.0)
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