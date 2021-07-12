package de.pbauerochse.worklogviewer.workitem.add.fx

import de.pbauerochse.worklogviewer.datasource.AddWorkItemRequest
import de.pbauerochse.worklogviewer.datasource.AddWorkItemResult
import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.fx.workitem.add.FetchWorkItemTypesTask
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.tasks.Tasks
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.Project
import de.pbauerochse.worklogviewer.timereport.WorkItemType
import de.pbauerochse.worklogviewer.trimToNull
import de.pbauerochse.worklogviewer.util.WorklogTimeFormatter
import de.pbauerochse.worklogviewer.workitem.add.AddWorkItemTask
import de.pbauerochse.worklogviewer.workitem.add.event.WorkItemAddedEvent
import de.pbauerochse.worklogviewer.workitem.add.fx.task.LoadIssueByReadableIdTask
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import java.time.LocalDate

class AddWorkItemModel() {

    /**
     * The [Issue] for which a [de.pbauerochse.worklogviewer.timereport.WorkItem] should be created
     */
    internal val selectedIssue: ObjectProperty<Issue?> = SimpleObjectProperty()

    /**
     * The `humanReadableId` of the [Issue]. Will be set automatically when `forIssueAtDate`
     * is invoked, but might be overwritten by the user, to create a [de.pbauerochse.worklogviewer.timereport.WorkItem]
     * for a different [Issue]
     */
    internal val issueId: StringProperty = SimpleStringProperty()

    /**
     * The title of the selected [Issue] if available
     */
    internal val issueTitle: StringProperty = SimpleStringProperty()

    /**
     * A prefilled [LocalDate] for the new [de.pbauerochse.worklogviewer.timereport.WorkItem]
     */
    internal val selectedDate: ObjectProperty<LocalDate?> = SimpleObjectProperty()

    /**
     * The duration of the new [de.pbauerochse.worklogviewer.timereport.WorkItem]
     * in the YouTrack style pattern (e.g. `1h 30m`)
     */
    internal val durationExpression: StringProperty = SimpleStringProperty()

    /**
     * The selected [WorkItemType] for the [de.pbauerochse.worklogviewer.timereport.WorkItem] to be created
     */
    internal val selectedWorkItemType: ObjectProperty<WorkItemType?> = SimpleObjectProperty()

    /**
     * An optional description for the [de.pbauerochse.worklogviewer.timereport.WorkItem]
     */
    internal val workItemDescription: StringProperty = SimpleStringProperty()

    /**
     * The valid [WorkItemType]s for the selected [Issue]
     */
    internal val workItemTypes: ObjectProperty<ObservableList<WorkItemType>> = SimpleObjectProperty(FXCollections.observableArrayList())

    /**
     * May contain an error that might have occurred
     */
    internal val errorMessage: StringProperty = SimpleStringProperty()

    private val isValidDuration: BooleanBinding = durationExpression.isNotEmpty.and(
        object : BooleanBinding() {
            init {
                bind(durationExpression)
            }

            override fun computeValue(): Boolean {
                return durationExpression.value
                    ?.let { WorklogTimeFormatter(SettingsUtil.settingsViewModel.workhoursProperty.value).parseDurationInMinutes(it) } != null
            }
        }
    )

    internal val isSubmittableWorkItem: BooleanBinding = issueId.isNotEmpty
        .and(isValidDuration)
        .and(selectedDate.isNotNull)

    fun forIssueAtDate(issue: Issue?, date: LocalDate?) {
        updateSelectedIssue(issue)
        selectedDate.set(date)
    }

    internal fun submitWorkItem(successCallback: () -> Unit) {
        val request = AddWorkItemRequest(
            issueId = issueId.value!!,
            date = selectedDate.value!!,
            durationInMinutes = WorklogTimeFormatter(SettingsUtil.settingsViewModel.workhoursProperty.value).parseDurationInMinutes(durationExpression.value)!!,
            workItemType = selectedWorkItemType.value,
            description = workItemDescription.value?.trimToNull()
        )

        val task = AddWorkItemTask(request).apply {
            onSucceeded = EventHandler {
                handleAddWorkItemResponse(it.source.value as AddWorkItemResult)
                successCallback.invoke()
            }
            onFailed = EventHandler { errorMessage.set(it.source.exception.message) }
        }

        Tasks.startTask(task)
    }

    internal fun updateIssueById(humanReadableIssueId: String) {
        val task = LoadIssueByReadableIdTask(humanReadableIssueId).apply {
            onSucceeded = EventHandler {
                updateSelectedIssue(value)
            }
            onFailed = EventHandler { errorMessage.set(it.source.exception.message) }
        }

        Tasks.startBackgroundTask(task)
    }

    private fun updateSelectedIssue(issue: Issue?) {
        issueId.set(issue?.humanReadableId)
        issueTitle.set(issue?.fullTitle)
        selectedIssue.set(issue)

        updateWorkItemTypes(issue?.project)
    }

    private fun handleAddWorkItemResponse(addWorkItemResult: AddWorkItemResult) {
        EventBus.publish(WorkItemAddedEvent(addWorkItemResult.issue, addWorkItemResult.addedWorkItem))
    }

    private fun updateWorkItemTypes(project: Project?) {
        workItemTypes.value.clear()

        project?.id?.let { projectId ->
            val task = FetchWorkItemTypesTask(projectId).apply {
                onSucceeded = EventHandler {
                    val types = (it.source as FetchWorkItemTypesTask).value
                    workItemTypes.value.setAll(listOf(null) + types)
                }
                onFailed = EventHandler { errorMessage.set(it.source.exception.message) }
            }

            Tasks.startBackgroundTask(task)
        }
    }

}