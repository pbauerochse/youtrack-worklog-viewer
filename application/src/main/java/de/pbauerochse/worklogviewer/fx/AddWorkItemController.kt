package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.addWorkItem
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemRequest
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemResult
import de.pbauerochse.worklogviewer.fx.state.ReportDataHolder
import de.pbauerochse.worklogviewer.fx.tasks.AddWorkItemTask
import de.pbauerochse.worklogviewer.fx.tasks.MainTaskRunner
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.trimToNull
import de.pbauerochse.worklogviewer.util.WorklogTimeFormatter
import javafx.beans.property.*
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.WindowEvent
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDate
import java.util.*

/**
 * Controller for the view to add a work item
 * to an issue
 */
class AddWorkItemController : Initializable {

    val issueProperty: StringProperty = SimpleStringProperty()
    val dateProperty: ObjectProperty<LocalDate?> = SimpleObjectProperty()
    private val durationProperty: StringProperty = SimpleStringProperty()
    private val isValidWorkTimeProperty: BooleanProperty = SimpleBooleanProperty()

    @FXML
    private lateinit var saveButton: Button

    @FXML
    private lateinit var cancelButton: Button

    @FXML
    private lateinit var issueTextField: TextField

    @FXML
    private lateinit var workDateDatePicker: DatePicker

    @FXML
    private lateinit var workDurationTextField: TextField

    @FXML
    private lateinit var workDescriptionTextField: TextField

    @FXML
    private lateinit var progressIndicator: StackPane

    @FXML
    private lateinit var progressBarContainer: VBox

    @FXML
    private lateinit var errorLabel: Label


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        issueTextField.textProperty().bindBidirectional(issueProperty)
        workDateDatePicker.valueProperty().bindBidirectional(dateProperty)
//        DatePickerManualEditListener.applyTo(workDateDatePicker)

        workDurationTextField.textProperty().bindBidirectional(durationProperty)
        workDurationTextField.textProperty().addListener { _, _, newDuration -> updateIsValidDurationProperty(newDuration) }
        errorLabel.visibleProperty().bind(errorLabel.textProperty().isNotEmpty)

        cancelButton.disableProperty().bind(progressIndicator.visibleProperty())
        saveButton.disableProperty().bind(
            progressIndicator.visibleProperty().or(durationProperty.isEmpty.or(isValidWorkTimeProperty.not()).or(issueProperty.isEmpty).or(dateProperty.isNull))
        )

        // workaround to detect whether the whole form has been rendered to screen yet
        val focusedElement = if (issueProperty.isEmpty.value) issueTextField else workDurationTextField
        focusedElement.requestFocus()
    }

    fun closeDialog() {
        LOGGER.debug("Closing AddWorkItem dialogue")
        val window = progressBarContainer.scene.window
        window.fireEvent(WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST))
    }

    fun createNewWorkItem() {
        LOGGER.info("Trying to save new WorkItem")
        val request = AddWorkItemRequest(
            issueId = issueProperty.value,
            date = dateProperty.value!!,
            durationInMinutes = parseWorkTimeFromField(workDurationTextField.text)!!,
            description = workDescriptionTextField.text.trimToNull()
        )

        val task = AddWorkItemTask(request).apply {
            onSucceeded = EventHandler { handleAddWorkItemResponse(it.source.value as AddWorkItemResult) }
            onFailed = EventHandler { handleError(it.source.exception) }
        }

        MainTaskRunner.startTask(task)
    }

    private fun updateIsValidDurationProperty(newDuration: String?) {
        isValidWorkTimeProperty.value = newDuration.isNullOrBlank().not() && isValidWorkTime(newDuration!!)
    }

    private fun isValidWorkTime(newDuration: String): Boolean {
        val parseWorkTimeFromField = parseWorkTimeFromField(newDuration)
        return parseWorkTimeFromField != null
    }

    private fun parseWorkTimeFromField(newDuration: String) = WorklogTimeFormatter(SettingsUtil.settingsViewModel.workhoursProperty.value).parseDurationInMinutes(newDuration)

    private fun handleAddWorkItemResponse(newWorkitem: AddWorkItemResult) {
        LOGGER.debug("Adding Work Item was successful. Adding $newWorkitem to current rpeort")
        val currentTimeReport = ReportDataHolder.currentTimeReportProperty.value
        currentTimeReport?.let {
            val newTimeReport = it.addWorkItem(newWorkitem)
            ReportDataHolder.currentTimeReportProperty.value = newTimeReport
        }

        closeDialog()
    }

    private fun handleError(throwable: Throwable) {
        LOGGER.warn("Error while adding new worklog item", throwable)
        errorLabel.text = throwable.localizedMessage
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AddWorkItemController::class.java)
    }

}
