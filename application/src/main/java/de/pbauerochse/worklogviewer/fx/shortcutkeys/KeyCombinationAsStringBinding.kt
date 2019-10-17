package de.pbauerochse.worklogviewer.fx.shortcutkeys

import javafx.beans.binding.StringBinding
import javafx.beans.property.ObjectProperty
import javafx.scene.input.KeyCombination

class KeyCombinationAsStringBinding(private val property : ObjectProperty<KeyCombination>) : StringBinding() {

    init {
        bind(property)
    }

    override fun dispose() {
        unbind(property)
    }

    override fun computeValue(): String? = property.value?.displayText
}