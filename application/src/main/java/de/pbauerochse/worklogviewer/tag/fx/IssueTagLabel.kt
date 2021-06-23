package de.pbauerochse.worklogviewer.tag.fx

import de.pbauerochse.worklogviewer.timereport.Tag
import javafx.scene.control.Label

/**
 * UI Component to display a [Tag]
 */
class IssueTagLabel(tag: Tag): Label() {

    init {
        text = tag.label
        style = listOfNotNull(
            tag.backgroundColorHex?.let { "-fx-background-color: $it;" },
            tag.foregroundColorHex?.let { "-fx-fill: $it;" },
            tag.foregroundColorHex?.let { "-fx-text-fill: $it;" }
        )
        .joinToString(separator = "\n")
        styleClass.add("issue-tag")
    }

}