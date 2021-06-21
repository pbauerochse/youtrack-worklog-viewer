package de.pbauerochse.worklogviewer

import de.pbauerochse.worklogviewer.fx.theme.ThemeChangeListener
import de.pbauerochse.worklogviewer.preloader.Preloader
import de.pbauerochse.worklogviewer.settings.SettingsUtil.saveSettings
import de.pbauerochse.worklogviewer.settings.SettingsUtil.settings
import de.pbauerochse.worklogviewer.settings.SettingsUtil.settingsViewModel
import de.pbauerochse.worklogviewer.tasks.Tasks.shutdown
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.application.Application
import javafx.beans.value.ObservableValue
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Main class to start the Java FX UI
 */
class WorklogViewer : Application() {

    private lateinit var primaryStage: Stage

    @Throws(Exception::class)
    override fun start(stage: Stage) {
        instance = this

        val settings = settings
        val settingsViewModel = settingsViewModel

        LOGGER.info("Java Version: {}", System.getProperty("java.version"))
        LOGGER.info("Default Locale: {}", Locale.getDefault())
        LOGGER.info("Default Charset: {}", Charset.defaultCharset())
        LOGGER.info("Default TimeZone: {}", TimeZone.getDefault().toZoneId())
        LOGGER.info("Theme: {}", settings.theme)

        Preloader.preload()

        val loader = FXMLLoader(StandardCharsets.UTF_8).apply {
            resources = FormattingUtil.RESOURCE_BUNDLE
        }

        val root = classPathResource("/fx/views/main.fxml")!!.use {
            loader.load<Parent>(it)
        }

        val mainScene = Scene(root, settings.windowSettings.width.toDouble(), settings.windowSettings.height.toDouble()).apply {
            stylesheets.add("/fx/css/base-styling.css")
            stylesheets.add(settingsViewModel.themeProperty.get().stylesheet)
        }
        settingsViewModel.themeProperty.addListener(ThemeChangeListener(mainScene))

        primaryStage = stage.apply {
            title = "YouTrack Worklog Viewer ${FormattingUtil.getFormatted("release.version")}"
            icons.addAll(listOf(
                classPathResource("/fx/img/icons/logo-128.png")!!.use { Image(it) },
                classPathResource("/fx/img/icons/logo-64.png")!!.use { Image(it) },
                classPathResource("/fx/img/icons/logo-32.png")!!.use { Image(it) }
            ))
            scene = mainScene

            x = settings.windowSettings.positionX.toDouble()
            y = settings.windowSettings.positionY.toDouble()
            width = settings.windowSettings.width.toDouble()
            height = settings.windowSettings.height.toDouble()
        }
        primaryStage.show()

        // settings listener
        stage.widthProperty().addListener { _: ObservableValue<out Number>?, _: Number?, newValue: Number -> settings.windowSettings.width = newValue.toInt() }
        stage.heightProperty().addListener { _: ObservableValue<out Number>?, _: Number?, newValue: Number -> settings.windowSettings.height = newValue.toInt() }
        stage.xProperty().addListener { _: ObservableValue<out Number>?, _: Number?, newValue: Number -> settings.windowSettings.positionX = newValue.toInt() }
        stage.yProperty().addListener { _: ObservableValue<out Number>?, _: Number?, newValue: Number -> settings.windowSettings.positionY = newValue.toInt() }
    }

    override fun stop() {
        saveSettings()
        shutdown()
    }

    fun requestShutdown() {
        LOGGER.debug("Shutdown requested")
        primaryStage.fireEvent(WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST))
    }

    private fun classPathResource(resource: String): InputStream? = WorklogViewer::class.java.getResource(resource)?.openStream()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorklogViewer::class.java)
        lateinit var instance: WorklogViewer
    }
}