package de.pbauerochse.worklogviewer.fx.shortcutkeys

import javafx.beans.property.ObjectProperty
import javafx.scene.input.KeyCombination

data class KeyboardShortcutDefinition(
    val label: String,
    val property: ObjectProperty<KeyCombination>
)