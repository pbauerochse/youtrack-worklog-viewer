package de.pbauerochse.worklogviewer.fx.shortcutkeys

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import java.util.*

/**
 * Records keystrokes to let the user
 * define a keyboard shortcut. Listening for
 * keystrokes can be cancelled by hitting the
 * Escape button
 */
class RecordKeyboardShortcutListener(scene: Scene, private val changeListener : (combination : KeyCombination) -> Unit) : EventHandler<KeyEvent> {

    var enabledProperty: BooleanProperty = SimpleBooleanProperty(false)

    init {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this)
    }

    override fun handle(event: KeyEvent) {
        if (enabledProperty.get() && !event.code.isModifierKey) {
            event.consume()
            if (event.code == KeyCode.ESCAPE) {
                enabledProperty.set(false)
            } else {
                val keyCombination = createCombo(event)
                changeListener.invoke(keyCombination)
                enabledProperty.set(false)
            }
        }
    }

    private fun createCombo(event: KeyEvent): KeyCombination {
        val modifiers = ArrayList<KeyCombination.Modifier>()
        if (event.isControlDown) {
            modifiers.add(KeyCombination.CONTROL_DOWN)
        }
        if (event.isMetaDown) {
            modifiers.add(KeyCombination.META_DOWN)
        }
        if (event.isAltDown) {
            modifiers.add(KeyCombination.ALT_DOWN)
        }
        if (event.isShiftDown) {
            modifiers.add(KeyCombination.SHIFT_DOWN)
        }
        return KeyCodeCombination(event.code, *modifiers.toTypedArray())
    }

}