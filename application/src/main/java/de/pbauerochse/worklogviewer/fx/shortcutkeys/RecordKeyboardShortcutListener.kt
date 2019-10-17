package de.pbauerochse.worklogviewer.fx.shortcutkeys

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
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
class RecordKeyboardShortcutListener(scene: Scene) : EventHandler<KeyEvent> {

    val enabledProperty: BooleanProperty = SimpleBooleanProperty(false)

    private var currentProperty : ObjectProperty<KeyCombination>? = null

    init {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this)
    }

    fun listen(property : ObjectProperty<KeyCombination>) {
        enabledProperty.set(true)
        currentProperty = property
    }


    override fun handle(event: KeyEvent) {
        if (enabledProperty.get() && !event.code.isModifierKey) {
            event.consume()
            if (event.code == KeyCode.ESCAPE) {
                stopListening()
            } else {
                val keyCombination = createCombo(event)
                currentProperty!!.set(keyCombination)
                stopListening()
            }
        }
    }

    private fun stopListening() {
        enabledProperty.set(false)
        currentProperty = null
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