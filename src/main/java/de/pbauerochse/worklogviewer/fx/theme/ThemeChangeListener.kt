package de.pbauerochse.worklogviewer.fx.theme

import de.pbauerochse.worklogviewer.fx.Theme
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Scene

/**
 * Listens for changes in a Theme property and applies the
 * new selected Theme to the Scene
 */
class ThemeChangeListener(private val scene: Scene) : ChangeListener<Theme> {

    override fun changed(observable: ObservableValue<out Theme>?, oldValue: Theme?, newValue: Theme?) {
        removeOldThemeStylesheet(newValue)
        newValue?.stylesheet.let { scene.stylesheets.add(it) }
    }

    private fun removeOldThemeStylesheet(newValue: Theme?) {
        Theme.values()
            .filter { it != newValue }
            .map { it.stylesheet }
            .forEach { scene.stylesheets.remove(it) }
    }
}