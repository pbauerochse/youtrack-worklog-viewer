package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.fx.theme.ThemeChangeListener
import de.pbauerochse.worklogviewer.plugin.PopupSpecification
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URL

fun Scene.openDialog(view: URL, specs: PopupSpecification) = openDialog(view.toExternalForm(), specs)

fun Scene.openDialog(view: String, specs: PopupSpecification) {
    val content = FXMLLoader.load<Parent>(MainViewController::class.java.getResource(view), FormattingUtil.RESOURCE_BUNDLE)
    openDialog(content, specs)
}

fun Scene.openDialog(content: Parent, specs: PopupSpecification) {
    val settingsViewModel = SettingsUtil.settingsViewModel

    val scene = Scene(content).apply {
        stylesheets.add("/fx/css/base-styling.css")
        stylesheets.add(settingsViewModel.themeProperty.get().stylesheet)
    }

    val themeChangeListener = ThemeChangeListener(scene)
    settingsViewModel.themeProperty.addListener(themeChangeListener)

    val stage = Stage().apply {
        this.initOwner(window)
        this.title = specs.title
        this.scene = scene
    }

    if (specs.modal) {
        stage.initStyle(StageStyle.UTILITY)
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.isResizable = false
    }

    specs.onClose?.let { onCloseCallback ->
        stage.setOnCloseRequest {
            settingsViewModel.themeProperty.removeListener(themeChangeListener)
            onCloseCallback.invoke()
        }
    }

    stage.showAndWait()
}