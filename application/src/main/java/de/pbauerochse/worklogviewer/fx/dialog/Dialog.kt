package de.pbauerochse.worklogviewer.fx.dialog

import de.pbauerochse.worklogviewer.fx.MainViewController
import de.pbauerochse.worklogviewer.fx.theme.ThemeChangeListener
import de.pbauerochse.worklogviewer.plugins.dialog.DialogCallback
import de.pbauerochse.worklogviewer.plugins.dialog.DialogSpecification
import de.pbauerochse.worklogviewer.plugins.dialog.FileChooserSpecification
import de.pbauerochse.worklogviewer.plugins.dialog.WorklogViewerDialog
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.net.URL

class Dialog(private val scene: Scene) : WorklogViewerDialog {

    override fun showFxml(fxmlUrl: URL, specs: DialogSpecification) {
        openDialog(fxmlUrl.toExternalForm(), specs)
    }

    fun openDialog(view: String, specs: DialogSpecification) {
        val content = FXMLLoader.load<Parent>(MainViewController::class.java.getResource(view), FormattingUtil.RESOURCE_BUNDLE)
        openDialog(content, specs)
    }

    fun openDialog(content: Parent, specs: DialogSpecification) {
        Platform.runLater {
            val settingsViewModel = SettingsUtil.settingsViewModel

            val scene = Scene(content).apply {
                stylesheets.add("/fx/css/base-styling.css")
                stylesheets.add(settingsViewModel.themeProperty.get().stylesheet)
            }

            val themeChangeListener = ThemeChangeListener(scene)
            settingsViewModel.themeProperty.addListener(themeChangeListener)

            val stage = Stage().apply {
                this.initOwner(scene.window)
                this.title = specs.title
                this.scene = scene
            }

            if (specs.modal) {
                stage.initModality(Modality.APPLICATION_MODAL)
                stage.isResizable = false
            }

            specs.onClose?.let { onCloseCallback ->
                stage.setOnCloseRequest {
                    settingsViewModel.themeProperty.removeListener(themeChangeListener)
                    onCloseCallback.invoke()
                }
            }

            // workaround to stop flickering when positioning stage on parent stage
            // see https://stackoverflow.com/a/44165221/5883577
            val widthListener = ChangeListener<Number> { _, _, _ -> centerDialog(stage) }
            val heightListener = ChangeListener<Number> { _, _, _ -> centerDialog(stage) }
            stage.widthProperty().addListener(widthListener)
            stage.heightProperty().addListener(heightListener)
            stage.onShown = EventHandler {
                val theStage = it.source as Stage
                theStage.widthProperty().removeListener(widthListener)
                theStage.heightProperty().removeListener(heightListener)
            }

            stage.showAndWait()
        }
    }

    private fun centerDialog(newStage: Stage) {
        val mainWindowSettingsViewController = SettingsUtil.settings.windowSettings

        val widthDifference = mainWindowSettingsViewController.width - newStage.width
        val heightDifference = mainWindowSettingsViewController.height - newStage.height

        newStage.x = mainWindowSettingsViewController.positionX + (widthDifference / 2)
        newStage.y = mainWindowSettingsViewController.positionY + (heightDifference / 2)
    }

    override fun showSaveFileDialog(specification: FileChooserSpecification, callback: DialogCallback) {
        Platform.runLater {
            val file = fileChooser(specification).showSaveDialog(scene.window)
            if (file != null) {
                SettingsUtil.settingsViewModel.lastUsedFilePath.set(file.parentFile.absolutePath)
                callback.invoke(file)
            }
        }
    }

    override fun showOpenFileDialog(specification: FileChooserSpecification, callback: DialogCallback) {
        Platform.runLater {
            val file = fileChooser(specification).showOpenDialog(scene.window)
            if (file != null) {
                SettingsUtil.settingsViewModel.lastUsedFilePath.set(file.parentFile.absolutePath)
                callback.invoke(file)
            }
        }
    }

    private fun fileChooser(specification: FileChooserSpecification): FileChooser {
        return FileChooser().apply {
            initialFileName = specification.initialFileName
            title = specification.title
            initialDirectory = SettingsUtil.settingsViewModel.lastUsedFilePath.value?.let { File(it) }?.takeIf { it.exists() && it.isDirectory } ?: File(System.getProperty("user.home"))
            selectedExtensionFilter = specification.fileType?.let {
                FileChooser.ExtensionFilter(it.description, it.extension)
            }
        }
    }

}