package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.WorklogViewer
import de.pbauerochse.worklogviewer.domain.ReportTimerange
import de.pbauerochse.worklogviewer.domain.timerangeprovider.TimerangeProviderFactory
import de.pbauerochse.worklogviewer.fx.components.tabs.TimeReportResultTabbedPane
import de.pbauerochse.worklogviewer.fx.converter.GroupByCategoryStringConverter
import de.pbauerochse.worklogviewer.fx.converter.ReportTimerangeStringConverter
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportTask
import de.pbauerochse.worklogviewer.fx.tasks.GetGroupByCategoriesTask
import de.pbauerochse.worklogviewer.fx.tasks.VersionCheckerTask
import de.pbauerochse.worklogviewer.fx.theme.ThemeChangeListener
import de.pbauerochse.worklogviewer.setHref
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.settings.SettingsViewModel
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.version.Version
import de.pbauerochse.worklogviewer.youtrack.TimeReport
import de.pbauerochse.worklogviewer.youtrack.TimeReportParameters
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory
import de.pbauerochse.worklogviewer.youtrack.domain.NoSelectionGroupByCategory
import javafx.beans.value.ChangeListener
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.URL
import java.time.LocalDate
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Java FX Controller for the main window
 */
class MainViewController : Initializable {

    @FXML
    private lateinit var timerangeComboBox: ComboBox<ReportTimerange>

    @FXML
    private lateinit var groupByCategoryComboBox: ComboBox<GroupByCategory>

    @FXML
    private lateinit var fetchWorklogButton: Button

    @FXML
    private lateinit var exportToExcelMenuItem: MenuItem

    @FXML
    private lateinit var settingsMenuItem: MenuItem

    @FXML
    private lateinit var logMessagesMenuItem: MenuItem

    @FXML
    private lateinit var aboutMenuItem: MenuItem

    @FXML
    private lateinit var exitMenuItem: MenuItem

    @FXML
    private lateinit var progressBar: ProgressBar

    @FXML
    private lateinit var progressText: Text

    @FXML
    private lateinit var resultTabPane: TimeReportResultTabbedPane

    @FXML
    private lateinit var waitScreenOverlay: StackPane

    @FXML
    private lateinit var startDatePicker: DatePicker

    @FXML
    private lateinit var endDatePicker: DatePicker

    @FXML
    private lateinit var mainToolbar: ToolBar

    private lateinit var resources: ResourceBundle
    private lateinit var settingsModel: SettingsViewModel

    override fun initialize(location: URL?, resources: ResourceBundle) {
        LOGGER.debug("Initializing main view")
        this.resources = resources
        this.settingsModel = SettingsUtil.settingsViewModel

        initializeTimerangeComboBox()
        initializeGroupByComboBox()
        initializeDatePickers()
        initializeFetchWorklogsButton()
        initializeMenuItems()

        checkForUpdate()

        // workaround to detect whether the whole form has been rendered to screen yet
        progressBar.sceneProperty().addListener { _, oldValue, newValue ->
            if (oldValue == null && newValue != null) {
                onFormShown()
            }
        }
    }

    private fun initializeTimerangeComboBox() {
        timerangeComboBox.apply {
            converter = ReportTimerangeStringConverter()
            items.addAll(*ReportTimerange.values())
            selectionModel.select(settingsModel.lastUsedReportTimerange)
            selectionModel.selectedItemProperty().addListener { _, _, selectedTimerange -> timerangeChanged(selectedTimerange) }
        }

        settingsModel.lastUsedReportTimerangeProperty().addListener { _, _, newValue -> timerangeComboBox.selectionModel.select(newValue) }
        timerangeChanged(settingsModel.lastUsedReportTimerangeProperty().value)
    }

    private fun timerangeChanged(newValue: ReportTimerange) {
        // prepopulate start and end datepickers and remove error labels
        val timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(newValue, startDatePicker.value, endDatePicker.value)
        startDatePicker.value = timerangeProvider.startDate
        endDatePicker.value = timerangeProvider.endDate
        settingsModel.lastUsedReportTimerangeProperty().set(newValue)
    }

    private fun initializeGroupByComboBox() {
        groupByCategoryComboBox.apply {
            disableProperty().bind(groupByCategoryComboBox.itemsProperty().isNull)
            converter = GroupByCategoryStringConverter(groupByCategoryComboBox)
        }

        // load group by criteria when connection parameters are present
        if (!settingsModel.hasMissingConnectionSettings) {
            loadGroupByCriteriaFromYouTrack()
        }
    }

    /**
     * Fetches groupBy criteria from YouTrack
     */
    private fun loadGroupByCriteriaFromYouTrack() {
        LOGGER.info("Fetching GroupByCategories")
        val task = GetGroupByCategoriesTask()
        task.setOnSucceeded { event ->
            val worker = event.source as GetGroupByCategoriesTask
            val categoryList = worker.value as List<GroupByCategory>
            LOGGER.info("{} succeeded with {} GroupByCategories", task.title, categoryList.size)

            groupByCategoryComboBox.items.add(NoSelectionGroupByCategory())
            groupByCategoryComboBox.items.addAll(categoryList.sortedBy { it.name })
            groupByCategoryComboBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                val lastUsed = newValue?.id
                settingsModel.lastUsedGroupByCategoryIdProperty().value = lastUsed
            }

            val lastUsedGroupById = settingsModel.lastUsedGroupByCategoryId
            var selectedItemIndex = 0

            for (i in 0 until groupByCategoryComboBox.items.size) {
                if (StringUtils.equals(groupByCategoryComboBox.items[i].id, lastUsedGroupById)) {
                    selectedItemIndex = i
                    break
                }
            }

            groupByCategoryComboBox.selectionModel.select(selectedItemIndex)
        }
        startTask(task)
    }

    private fun initializeDatePickers() {
        // start and end datepicker are only editable if report timerange is CUSTOM
        val startDateChangeListener = ChangeListener<LocalDate> { _, _, newStartDate ->
            LOGGER.info("Setting start date to {}", newStartDate)
            if (newStartDate == null || endDatePicker.value != null && endDatePicker.value.isBefore(newStartDate)) {
                startDatePicker.styleClass.add(REQUIRED_FIELD_CLASS)
            } else {
                startDatePicker.styleClass.remove(REQUIRED_FIELD_CLASS)
            }
        }

        val endDateChangeListener = ChangeListener<LocalDate> { _, _, newEndDate ->
            LOGGER.info("Setting end date to {}", newEndDate)
            if (newEndDate == null || startDatePicker.value != null && startDatePicker.value.isAfter(newEndDate)) {
                endDatePicker.styleClass.add(REQUIRED_FIELD_CLASS)
            } else {
                endDatePicker.styleClass.remove(REQUIRED_FIELD_CLASS)
            }
        }

        startDatePicker.disableProperty().bind(timerangeComboBox.selectionModel.selectedItemProperty().isNotEqualTo(ReportTimerange.CUSTOM))
        startDatePicker.valueProperty().addListener(startDateChangeListener)

        endDatePicker.disableProperty().bind(timerangeComboBox.selectionModel.selectedItemProperty().isNotEqualTo(ReportTimerange.CUSTOM))
        endDatePicker.valueProperty().addListener(endDateChangeListener)

        // value listener
        startDatePicker.valueProperty().addListener { _, _, newValue -> settingsModel.startDateProperty().set(newValue) }
        endDatePicker.valueProperty().addListener { _, _, newValue -> settingsModel.endDateProperty().set(newValue) }

        // set value
        if (settingsModel.lastUsedReportTimerange == ReportTimerange.CUSTOM) {
            startDatePicker.value = settingsModel.startDate
            endDatePicker.value = settingsModel.endDate
            startDateChangeListener.changed(startDatePicker.valueProperty(), null, startDatePicker.value)
            endDateChangeListener.changed(endDatePicker.valueProperty(), null, endDatePicker.value)
        }
    }

    private fun initializeFetchWorklogsButton() {
        // fetch worklog button click
        fetchWorklogButton.disableProperty().bind(settingsModel.hasMissingConnectionSettingsProperty())
        fetchWorklogButton.setOnAction { _ -> fetchWorklogs() }
    }

    private fun initializeMenuItems() {
        // export to excel only possible if resultTabPane is not empty and therefore seems to contain data
        exportToExcelMenuItem.disableProperty().bind(resultTabPane.selectionModel.selectedItemProperty().isNull)

        // menu items click actions
        exportToExcelMenuItem.setOnAction { _ -> startExportToExcelTask() }

        settingsMenuItem.setOnAction { _ -> showSettingsDialogue() }
        exitMenuItem.setOnAction { _ -> WorklogViewer.getInstance().requestShutdown() }
        logMessagesMenuItem.setOnAction { _ -> showLogMessagesDialogue() }
        aboutMenuItem.setOnAction { _ -> showAboutDialogue() }
    }

    private fun checkForUpdate() {
        val versionCheckTask = VersionCheckerTask()
        versionCheckTask.onSucceeded = EventHandler { this.addDownloadLinkToToolbarIfNeverVersionPresent(it) }
        startTask(versionCheckTask)
    }

    private fun addDownloadLinkToToolbarIfNeverVersionPresent(event: WorkerStateEvent) {
        val gitHubVersionOptional = (event.source as VersionCheckerTask).value
        gitHubVersionOptional.ifPresent { gitHubVersion ->
            val currentVersion = Version(resources.getString("release.version"))
            val mostRecentVersion = Version(gitHubVersion.version)

            LOGGER.debug("Most recent github version is {}, this version is {}", mostRecentVersion, currentVersion)
            if (mostRecentVersion.isNewerThan(currentVersion)) {
                val link = Hyperlink(getFormatted("worker.updatecheck.available", mostRecentVersion.toString()))
                link.setHref(gitHubVersion.url)
                mainToolbar.items.add(link)
            }
        }
    }

    private fun onFormShown() {
        LOGGER.debug("MainForm shown")

        if (settingsModel.hasMissingConnectionSettings!!) {
            LOGGER.info("No YouTrack connection settings defined yet. Opening settings dialogue")
            showSettingsDialogue()
        }

        // auto load data if a named timerange was selected
        // and the user chose to load data at startup
        if (timerangeComboBox.selectionModel.selectedItem != ReportTimerange.CUSTOM && settingsModel.loadDataAtStartupProperty().get()) {
            LOGGER.debug("loadDataAtStartup set. Loading report for {}", timerangeComboBox.selectionModel.selectedItem.name)
            fetchWorklogButton.fire()
        }
    }


    /**
     * Exports the currently visible data to an excel spreadsheet
     */
    private fun startExportToExcelTask() {
        val tab = resultTabPane.currentlyVisibleTab
        val task = tab.getDownloadAsExcelTask()
        if (task != null) {
            task.setOnSucceeded { e ->
                LOGGER.info("Excel creation succeeded")
                val file = e.source.value as File
                progressText.text = getFormatted("exceptions.excel.success", file.absolutePath)
            }
            startTask(task)
        }
    }

    /**
     * Fetches the worklogs for the currently defined settings from YouTrack
     */
    private fun fetchWorklogs() {
        val timerange = timerangeComboBox.selectionModel.selectedItem
        LOGGER.debug("Fetch worklogs clicked for timerange {}", timerange.toString())

        val selectedStartDate = startDatePicker.value
        val selectedEndDate = endDatePicker.value

        val timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(timerange, selectedStartDate, selectedEndDate)
        val groupByCategory = groupByCategoryComboBox.selectionModel.selectedItem
        val parameters = TimeReportParameters(timerangeProvider, groupByCategory)

        val task = FetchTimereportTask(parameters)
        task.setOnSucceeded { event -> displayWorklogResult(event.source.value as TimeReport) }
        startTask(task)
    }

    /**
     * Starts a thread performing the given task
     *
     * @param task The task to perform
     */
    private fun startTask(task: Task<*>) {
        LOGGER.info("Starting task {}", task.title)
        val initialOnRunningHandler = task.onRunning
        task.setOnRunning { event ->
            waitScreenOverlay.isVisible = true
            progressText.textProperty().bind(task.messageProperty())
            progressBar.progressProperty().bind(task.progressProperty())

            initialOnRunningHandler?.handle(event)
        }

        // success handler
        val onSucceededEventHandler = task.onSucceeded
        task.setOnSucceeded { event ->
            LOGGER.info("Task {} succeeded", task.title)

            // unbind progress indicators
            progressText.textProperty().unbind()
            progressBar.progressProperty().unbind()

            if (onSucceededEventHandler != null) {
                LOGGER.debug("Delegating Event to previous onSucceeded event handler")
                onSucceededEventHandler.handle(event)
            }

            waitScreenOverlay.isVisible = false
        }

        // error handler
        val onFailedEventHandler = task.onFailed
        task.setOnFailed { event ->
            LOGGER.warn("Task {} failed", task.title)

            // unbind progress indicators
            progressText.textProperty().unbind()
            progressBar.progressProperty().unbind()

            if (onFailedEventHandler != null) {
                LOGGER.debug("Delegating Event to previous onFailed event handler")
                onFailedEventHandler.handle(event)
            }

            val throwable = event.source.exception
            if (throwable != null && StringUtils.isNotBlank(throwable.message)) {
                LOGGER.warn("Showing error to user", throwable)
                progressText.text = throwable.message
            } else {
                if (throwable != null) {
                    LOGGER.warn("Error executing task {}", task.toString(), throwable)
                }

                progressText.text = getFormatted("exceptions.main.worker.unknown")
            }

            progressBar.progress = 1.0
            waitScreenOverlay.isVisible = false
        }

        // state change listener just for logging purposes
        task.stateProperty().addListener { _, oldValue, newValue -> LOGGER.debug("Task {} changed from {} to {}", task.title, oldValue, newValue) }

        EXECUTOR.submit(task)
    }

    private fun displayWorklogResult(timeReport: TimeReport) {
        LOGGER.info("Presenting TimeReport to the user")
        resultTabPane.update(timeReport)
    }

    private fun showSettingsDialogue() {
        LOGGER.debug("Showing settings dialogue")

        // pass in a handler to fetch the group by categories if connection
        // parameters get set
        openDialogue("/fx/views/settings.fxml", "view.settings.title", true) {
            if (!settingsModel.hasMissingConnectionSettings && groupByCategoryComboBox.items.size == 0) {
                LOGGER.debug("Settings window closed, connection settings set and groupBy combobox empty -> trying to fetch groupByCategories")
                loadGroupByCriteriaFromYouTrack()
            }
        }
    }

    private fun showLogMessagesDialogue() {
        LOGGER.debug("Showing log messages dialogue")
        openDialogue("/fx/views/logMessagesView.fxml", "view.menu.help.logs")
    }

    private fun showAboutDialogue() {
        LOGGER.debug("Showing log messages dialogue")
        openDialogue("/fx/views/about.fxml", "view.menu.help.about")
    }

    private fun openDialogue(view: String, titleResourceKey: String, modal: Boolean = false, onCloseCallback: (() -> Unit)? = null) {
        try {
            val content = FXMLLoader.load<Parent>(MainViewController::class.java.getResource(view), resources)
            val settingsViewModel = SettingsUtil.settingsViewModel

            val scene = Scene(content)
            scene.stylesheets.add("/fx/css/base-styling.css")
            scene.stylesheets.add(settingsViewModel.theme.stylesheet)

            val themeChangeListener = ThemeChangeListener(scene)
            settingsViewModel.themeProperty().addListener(themeChangeListener)

            val stage = Stage()
            stage.initOwner(progressBar.scene.window)

            if (modal) {
                stage.initStyle(StageStyle.UTILITY)
                stage.initModality(Modality.APPLICATION_MODAL)
                stage.isResizable = false
            }

            stage.title = getFormatted(titleResourceKey)
            stage.scene = scene

            if (onCloseCallback != null) {
                stage.setOnCloseRequest { _ ->
                    LOGGER.debug("View {} got close request. Notifying callback", view)
                    settingsViewModel.themeProperty().removeListener(themeChangeListener)
                    onCloseCallback()
                }
            }

            stage.showAndWait()
        } catch (e: IOException) {
            LOGGER.error("Could not open dialogue {}", view, e)
            throw ExceptionUtil.getRuntimeException("exceptions.view.io", e, view)
        }

    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MainViewController::class.java)
        private const val REQUIRED_FIELD_CLASS = "required"
        var EXECUTOR = ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, LinkedBlockingQueue())
    }
}
