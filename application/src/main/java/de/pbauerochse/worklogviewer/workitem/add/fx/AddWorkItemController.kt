package de.pbauerochse.worklogviewer.workitem.add.fx

import de.pbauerochse.worklogviewer.fx.listener.DatePickerManualEditListener
import de.pbauerochse.worklogviewer.timereport.WorkItemType
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.WindowEvent
import javafx.util.StringConverter
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*

/**
 * Controller for the view to add a work item
 * to an issue
 */
class AddWorkItemController : Initializable {

    internal val model: AddWorkItemModel = AddWorkItemModel()

    lateinit var saveButton: Button
    lateinit var cancelButton: Button
    lateinit var issueTextField: TextField
    lateinit var workDateDatePicker: DatePicker
    lateinit var workDurationTextField: TextField
    lateinit var workDescriptionTextField: TextField
    lateinit var progressIndicator: StackPane
    lateinit var progressBarContainer: VBox
    lateinit var errorLabel: Label
    lateinit var workTypeComboBox: ComboBox<WorkItemType>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        issueTextField.apply {
            disableProperty().bind(model.selectedIssue.isNotNull)
            textProperty().bindBidirectional(model.issueId)
        }

        workDateDatePicker.valueProperty().bindBidirectional(model.selectedDate)
        DatePickerManualEditListener.applyTo(workDateDatePicker)

        workDurationTextField.textProperty().bindBidirectional(model.durationExpression)
        errorLabel.apply {
            visibleProperty().bind(errorLabel.textProperty().isNotEmpty)
            textProperty().bind(model.errorMessage)
        }

        cancelButton.disableProperty().bind(progressIndicator.visibleProperty())
        saveButton.disableProperty().bind(progressIndicator.visibleProperty().or(model.isSubmittableWorkItem.not()))
        workDescriptionTextField.textProperty().bindBidirectional(model.workItemDescription)

        workTypeComboBox.apply {
            disableProperty().bind(model.workItemTypes.isNull)
            itemsProperty().bind(model.workItemTypes)
            valueProperty().bindBidirectional(model.selectedWorkItemType)
            converter = object : StringConverter<WorkItemType>() {
                override fun toString(type: WorkItemType?): String? = type?.label ?: type?.id
                override fun fromString(name: String?): WorkItemType? = name?.let { workTypeComboBox.items.find { item -> item.label == it }!! }
            }
        }

        // workaround to detect whether the whole form has been rendered to screen yet
        saveButton.sceneProperty().addListener { _, oldValue, newValue ->
            if (oldValue == null && newValue != null) {
                onFormShown()
            }
        }
    }

    private fun onFormShown() {
        focusBestInputElement()
    }

    fun closeDialog() {
        LOGGER.debug("Closing AddWorkItem dialogue")
        val window = progressBarContainer.scene.window
        window.fireEvent(WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST))
    }

    fun createNewWorkItem() {
        model.submitWorkItem {
            closeDialog()
        }
    }

    private fun focusBestInputElement() {
        val focusedElement = when {
            model.selectedIssue.isNull.value -> issueTextField
            model.selectedDate.isNull.value -> workDateDatePicker
            else -> workDurationTextField
        }
        focusedElement.requestFocus()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AddWorkItemController::class.java)
    }

}
