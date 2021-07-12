package de.pbauerochse.worklogviewer.workitem.add.fx

import de.pbauerochse.worklogviewer.fx.listener.DatePickerManualEditListener
import de.pbauerochse.worklogviewer.setHref
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
    lateinit var issueTitleLink: Hyperlink
    lateinit var workDateDatePicker: DatePicker
    lateinit var workDurationTextField: TextField
    lateinit var workDescriptionTextField: TextField
    lateinit var progressIndicator: StackPane
    lateinit var progressBarContainer: VBox
    lateinit var errorLabel: Label
    lateinit var workTypeComboBox: ComboBox<WorkItemType>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        issueTextField.apply {
            visibleProperty().bind(model.selectedIssue.isNull)
            managedProperty().bind(model.selectedIssue.isNull)
            textProperty().bindBidirectional(model.issueId)
        }

        issueTitleLink.apply {
            visibleProperty().bind(model.selectedIssue.isNotNull)
            managedProperty().bind(model.selectedIssue.isNotNull)
            textProperty().bind(model.issueTitle)
            tooltip = Tooltip().apply { textProperty().bind(model.issueTitle) }
        }
        model.selectedIssue.addListener { _, _, issue -> issue?.let { issueTitleLink.setHref(it.externalUrl.toExternalForm()) } }

        if (model.selectedIssue.isNull.value) {
            attachIssueChangeListener()
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

    private fun attachIssueChangeListener() {
        issueTextField.focusedProperty().addListener { _, wasPreviouslyFocused, isCurrentlyFocused ->
            if (wasPreviouslyFocused && !isCurrentlyFocused && !issueTextField.text.isNullOrBlank()) {
                model.updateIssueById(issueTextField.text)
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
