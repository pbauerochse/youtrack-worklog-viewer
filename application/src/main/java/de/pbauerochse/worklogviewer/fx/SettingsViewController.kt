package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.WorklogViewer
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.fx.converter.WorkhoursStringConverter
import de.pbauerochse.worklogviewer.fx.converter.YouTrackVersionStringConverter
import de.pbauerochse.worklogviewer.fx.shortcutkeys.KeyCombinationAsStringBinding
import de.pbauerochse.worklogviewer.fx.shortcutkeys.KeyboardShortcutDefinition
import de.pbauerochse.worklogviewer.fx.shortcutkeys.RecordKeyboardShortcutListener
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.settings.SettingsViewModel
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import javafx.stage.WindowEvent
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import java.util.stream.Stream

/**
 * FX Controller for the settings dialog
 */
class SettingsViewController : Initializable {

    @FXML
    private lateinit var youtrackUrlField: TextField
    @FXML
    private lateinit var youtrackVersionField: ComboBox<YouTrackVersion>
    @FXML
    private lateinit var youtrackUsernameField: TextField
    @FXML
    private lateinit var youtrackPermanentTokenField: PasswordField
    @FXML
    private lateinit var workhoursComboBox: ComboBox<Float>
    @FXML
    private lateinit var themeComboBox: ComboBox<Theme>
    @FXML
    private lateinit var showAllWorklogsCheckBox: CheckBox
    @FXML
    private lateinit var showStatisticsCheckBox: CheckBox
    @FXML
    private lateinit var loadDataAtStartupCheckBox: CheckBox
    @FXML
    private lateinit var showDecimalsInExcel: CheckBox
    @FXML
    private lateinit var enablePlugins: CheckBox
    @FXML
    private lateinit var saveSettingsButton: Button
    @FXML
    private lateinit var cancelSettingsButton: Button
    @FXML
    private lateinit var mondayCollapseCheckbox: CheckBox
    @FXML
    private lateinit var tuesdayCollapseCheckbox: CheckBox
    @FXML
    private lateinit var wednesdayCollapseCheckbox: CheckBox
    @FXML
    private lateinit var thursdayCollapseCheckbox: CheckBox
    @FXML
    private lateinit var fridayCollapseCheckbox: CheckBox
    @FXML
    private lateinit var saturdayCollapseCheckbox: CheckBox
    @FXML
    private lateinit var sundayCollapseCheckbox: CheckBox

    @FXML
    private lateinit var mondayHighlightCheckbox: CheckBox
    @FXML
    private lateinit var tuesdayHighlightCheckbox: CheckBox
    @FXML
    private lateinit var wednesdayHighlightCheckbox: CheckBox
    @FXML
    private lateinit var thursdayHighlightCheckbox: CheckBox
    @FXML
    private lateinit var fridayHighlightCheckbox: CheckBox
    @FXML
    private lateinit var saturdayHighlightCheckbox: CheckBox
    @FXML
    private lateinit var sundayHighlightCheckbox: CheckBox
    @FXML
    private lateinit var keyboardShortcutGridPane: GridPane

    override fun initialize(location: URL, resources: ResourceBundle) {
        LOGGER.debug("Initializing")
        val viewModel = SettingsUtil.settingsViewModel

        attachListeners(viewModel)
        initializeDefaultValues()
        bindInputElements(viewModel)

        themeComboBox.sceneProperty().addListener { _, _, scene ->
            if (scene != null) {
                initializeKeyboardShortcutPanel(
                    scene,
                    listOf(
                        KeyboardShortcutDefinition(getFormatted("view.settings.shortkey.fetchworklogs"), viewModel.fetchWorklogsKeyboardCombination),
                        KeyboardShortcutDefinition(getFormatted("view.settings.shortkey.showissuesearch"), viewModel.showIssueSearchKeyboardCombination),
                        KeyboardShortcutDefinition(getFormatted("view.settings.shortkey.showsettings"), viewModel.showSettingsKeyboardCombination),
                        KeyboardShortcutDefinition(getFormatted("view.settings.shortkey.togglestatistics"), viewModel.toggleStatisticsKeyboardCombination),
                        KeyboardShortcutDefinition(getFormatted("view.settings.shortkey.exit"), viewModel.exitWorklogViewerKeyboardCombination)
                    )
                )
            }
        }
    }

    private fun initializeDefaultValues() {
        // Workhours Combobox
        Stream.iterate(1f, { n -> n <= 25f }, { n -> n!! + 0.25f }).forEach { workhoursComboBox.items.add(it) }
        workhoursComboBox.converter = WorkhoursStringConverter

        // Version Combobox Values
        youtrackVersionField.items.addAll(YouTrackConnectorLocator.getSupportedVersions())
        youtrackVersionField.converter = YouTrackVersionStringConverter

        // Theme Combobox
        themeComboBox.items.addAll(*Theme.values())
    }

    private fun bindInputElements(viewModel: SettingsViewModel) {
        youtrackUrlField.textProperty().bindBidirectional(viewModel.youTrackUrlProperty)
        youtrackVersionField.valueProperty().bindBidirectional(viewModel.youTrackVersionProperty)
        youtrackUsernameField.textProperty().bindBidirectional(viewModel.youTrackUsernameProperty)
        youtrackPermanentTokenField.textProperty().bindBidirectional(viewModel.youTrackPermanentTokenProperty)

        themeComboBox.valueProperty().bindBidirectional(viewModel.themeProperty)
        workhoursComboBox.valueProperty().bindBidirectional(viewModel.workhoursProperty.asObject())
        showAllWorklogsCheckBox.selectedProperty().bindBidirectional(viewModel.showAllWorklogsProperty)
        showStatisticsCheckBox.selectedProperty().bindBidirectional(viewModel.showStatisticsProperty)
        loadDataAtStartupCheckBox.selectedProperty().bindBidirectional(viewModel.loadDataAtStartupProperty)
        showDecimalsInExcel.selectedProperty().bindBidirectional(viewModel.showDecimalsInExcelProperty)
        enablePlugins.selectedProperty().bindBidirectional(viewModel.enablePluginsProperty)

        mondayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateMondayProperty)
        tuesdayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateTuesdayProperty)
        wednesdayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateWednesdayProperty)
        thursdayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateThursdayProperty)
        fridayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateFridayProperty)
        saturdayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateSaturdayProperty)
        sundayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateSundayProperty)

        mondayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateMondayProperty)
        tuesdayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateTuesdayProperty)
        wednesdayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateWednesdayProperty)
        thursdayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateThursdayProperty)
        fridayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateFridayProperty)
        saturdayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateSaturdayProperty)
        sundayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateSundayProperty)
    }

    private fun initializeKeyboardShortcutPanel(scene: Scene, shortcutDefinitions: List<KeyboardShortcutDefinition>) {
        val recordKeyboardShortcutListener = RecordKeyboardShortcutListener(scene)

        val cancelHintLabel = Label(getFormatted("view.settings.shortkey.cancelhint"))
        cancelHintLabel.styleClass.add("hint-label")
        cancelHintLabel.visibleProperty().bind(recordKeyboardShortcutListener.enabledProperty)

        shortcutDefinitions.forEachIndexed { index, definition ->
            val descriptionLabel = Label(definition.label)
            val anchorPane = AnchorPane()
            keyboardShortcutGridPane.addRow(index, descriptionLabel, anchorPane)

            GridPane.setColumnIndex(descriptionLabel, 0)
            GridPane.setColumnIndex(anchorPane, 1)
            GridPane.setValignment(anchorPane, VPos.CENTER)
            GridPane.setMargin(anchorPane, Insets(2.0, 0.0, 0.0, 0.0))

            val assignedShortcutLabel = Label()
            assignedShortcutLabel.textProperty().bind(KeyCombinationAsStringBinding(definition.property))

            val assignShortcutButton = Button(getFormatted("view.settings.shortkey.define"))
            assignShortcutButton.disableProperty().bind(recordKeyboardShortcutListener.enabledProperty)
            assignShortcutButton.onAction = EventHandler { recordKeyboardShortcutListener.listen(definition.property) }

            anchorPane.children.addAll(assignedShortcutLabel, assignShortcutButton)
            AnchorPane.setRightAnchor(assignShortcutButton, 0.0)

            keyboardShortcutGridPane.rowConstraints.add(RowConstraints(10.0, 30.0, 30.0))
        }

        keyboardShortcutGridPane.addRow(keyboardShortcutGridPane.rowCount, cancelHintLabel)
        GridPane.setColumnIndex(cancelHintLabel, 1)
        GridPane.setMargin(cancelHintLabel, Insets(4.0, 0.0, 0.0, 0.0))
    }

    private fun attachListeners(viewModel: SettingsViewModel) {
        val hadMissingSettingsWhenOpened = SimpleBooleanProperty(viewModel.hasMissingConnectionSettings.get())

        // enable cancel button only when settings are valid or have been valid
        // when the form was shown, to allow the user cancel invalid inputs
        cancelSettingsButton.disableProperty().bind(hadMissingSettingsWhenOpened.and(viewModel.hasMissingConnectionSettings))

        cancelSettingsButton.setOnAction {
            LOGGER.debug("Cancel clicked")
            viewModel.discardChanges()
            closeSettingsDialogue()
        }

        saveSettingsButton.disableProperty().bind(viewModel.hasMissingConnectionSettings)
        saveSettingsButton.setOnAction {
            LOGGER.debug("Save settings clicked")
            viewModel.saveChanges()
            closeSettingsDialogue()
        }
    }

    @FXML
    fun showSettingsHelp() {
        val helpUrl = getFormatted("view.settings.authentication.help_url")
        LOGGER.debug("Opening page {} in browser", helpUrl)
        Platform.runLater { WorklogViewer.instance.hostServices.showDocument(helpUrl) }
    }

    private fun closeSettingsDialogue() {
        LOGGER.debug("Closing settings dialogue")
        val window = saveSettingsButton.scene.window
        window.fireEvent(WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST))
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SettingsViewController::class.java)
    }

}
