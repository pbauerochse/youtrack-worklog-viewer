package de.pbauerochse.worklogviewer.fx.converter

import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.grouping.Grouping
import javafx.scene.control.ComboBox
import javafx.util.StringConverter

class GroupingComboBoxConverter(private val categoryComboBox: ComboBox<Grouping>) : StringConverter<Grouping>() {

    override fun toString(category: Grouping?): String {
        return category?.label ?: getFormatted("grouping.none")
    }

    override fun fromString(categoryName: String?): Grouping? {
        // special "nothing-selected" item
        return if (getFormatted("grouping.none") == categoryName) {
            null
        } else categoryComboBox.items.firstOrNull { it.label == categoryName }
    }
}
