package de.pbauerochse.worklogviewer.fx.workitem.add

import de.pbauerochse.worklogviewer.addWorkItem
import de.pbauerochse.worklogviewer.datasource.AddWorkItemRequest
import de.pbauerochse.worklogviewer.datasource.AddWorkItemResult
import de.pbauerochse.worklogviewer.fx.listener.DatePickerManualEditListener
import de.pbauerochse.worklogviewer.fx.state.ReportDataHolder
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.tasks.DefaultTaskExecutor
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.WorkItemType
import de.pbauerochse.worklogviewer.trimToNull
import de.pbauerochse.worklogviewer.util.WorklogTimeFormatter
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.event.EventHandler
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.WindowEvent
import javafx.util.StringConverter
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDate
import java.util.*

/**
 * Controller for the view to add a work item
 * to an issue
 */
class AddWorkItemController : Initializable {

    private val issueIdProperty: StringProperty = SimpleStringProperty()
    private val projectIdProperty: StringProperty = SimpleStringProperty()
    private val dateProperty: ObjectProperty<LocalDate?> = SimpleObjectProperty()
    private val durationProperty: StringProperty = SimpleStringProperty()
    private val isValidWorkTimeProperty: BooleanProperty = SimpleBooleanProperty()

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
        issueTextField.textProperty().bindBidirectional(issueIdProperty)
        workDateDatePicker.valueProperty().bindBidirectional(dateProperty)
        DatePickerManualEditListener.applyTo(workDateDatePicker)

        workDurationTextField.textProperty().bindBidirectional(durationProperty)
        workDurationTextField.textProperty().addListener { _, _, newDuration -> updateIsValidDurationProperty(newDuration) }
        errorLabel.visibleProperty().bind(errorLabel.textProperty().isNotEmpty)

        cancelButton.disableProperty().bind(progressIndicator.visibleProperty())
        saveButton.disableProperty().bind(
            progressIndicator.visibleProperty().or(durationProperty.isEmpty.or(isValidWorkTimeProperty.not()).or(issueIdProperty.isEmpty).or(dateProperty.isNull))
        )

        workTypeComboBox.apply {
            disableProperty().bind(Bindings.isEmpty(workTypeComboBox.items))
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

    fun closeDialog() {
        LOGGER.debug("Closing AddWorkItem dialogue")
        val window = progressBarContainer.scene.window
        window.fireEvent(WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST))
    }

    fun createNewWorkItem() {
        LOGGER.info("Trying to save new WorkItem")
        val request = AddWorkItemRequest(
            issueId = issueIdProperty.value,
            date = dateProperty.value!!,
            durationInMinutes = parseWorkTimeFromField(workDurationTextField.text)!!,
            workItemType = workTypeComboBox.value,
            description = workDescriptionTextField.text.trimToNull()
        )

        val task = AddWorkItemTask(request).apply {
            onSucceeded = EventHandler { handleAddWorkItemResponse(it.source.value as AddWorkItemResult) }
            onFailed = EventHandler { handleError(it.source.exception) }
        }

        DefaultTaskExecutor.startTask(task)
    }

    private fun onFormShown() {
        focusBestInputElement()
        loadValidWorkTypes()
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

    private fun focusBestInputElement() {
        val focusedElement = if (issueIdProperty.isEmpty.value) issueTextField else workDurationTextField
        focusedElement.requestFocus()
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadValidWorkTypes() {
        projectIdProperty.value?.let { projectId ->
            val task = FetchWorkItemTypesTask(projectId).apply {
                onSucceeded = EventHandler { handleWorkItemTypes(it.source.value as List<WorkItemType>) }
                onFailed = EventHandler { handleError(it.source.exception) }
            }

            DefaultTaskExecutor.startTask(task)
        }
    }

    private fun handleWorkItemTypes(workitemTypes: List<WorkItemType>) {
        LOGGER.debug("Got ${workitemTypes.size} WorkItem types: $workitemTypes")
        workTypeComboBox.items.setAll(workitemTypes)
    }

    fun forIssueAtDate(issue: Issue?, date: LocalDate?) {
        issueIdProperty.set(issue?.id)
        projectIdProperty.set(issue?.project?.shortName)
        dateProperty.set(date)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AddWorkItemController::class.java)
    }

}
