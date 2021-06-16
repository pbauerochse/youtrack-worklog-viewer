package de.pbauerochse.worklogviewer.search.fx.results

import de.pbauerochse.worklogviewer.timereport.Field
import javafx.geometry.Orientation
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.HBox

/**
 * Contains a name-value pair of a field of an [de.pbauerochse.worklogviewer.timereport.Issue]
 */
class SearchResultIssueField(field: Field) : HBox() {

    init {
        styleClass.add("search-result-issue-field")
        children.addAll(
            Label("${field.name}:").apply {
                styleClass.add("field-name")
            },
            Label(field.value.joinToString().takeIf { it.isNotBlank() } ?: "-").apply {
                styleClass.add("field-value")
            },
            Separator(Orientation.VERTICAL)
        )
    }

}