package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.WorklogViewer
import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.fx.components.plugins.PluginMenu
import de.pbauerochse.worklogviewer.fx.components.plugins.PluginToolbarActionGroup
import de.pbauerochse.worklogviewer.fx.components.tabs.TimeReportResultTabbedPane
import de.pbauerochse.worklogviewer.fx.converter.GroupingComboBoxConverter
import de.pbauerochse.worklogviewer.fx.converter.TimerangeProviderStringConverter
import de.pbauerochse.worklogviewer.fx.dialog.Dialog
import de.pbauerochse.worklogviewer.fx.listener.DatePickerManualEditListener
import de.pbauerochse.worklogviewer.fx.plugins.PluginActionContextAdapter
import de.pbauerochse.worklogviewer.fx.plugins.WorklogViewerStateAdapter
import de.pbauerochse.worklogviewer.fx.plugins.WorklogviewerUiState
import de.pbauerochse.worklogviewer.fx.state.ReportDataHolder.currentTimeReportProperty
import de.pbauerochse.worklogviewer.fx.tasks.CheckForUpdateTask
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportTask
import de.pbauerochse.worklogviewer.plugins.PluginLoader
import de.pbauerochse.worklogviewer.plugins.actions.PluginActionContext
import de.pbauerochse.worklogviewer.plugins.dialog.DialogSpecification
import de.pbauerochse.worklogviewer.plugins.state.WorklogViewerState
import de.pbauerochse.worklogviewer.plugins.tasks.PluginTask
import de.pbauerochse.worklogviewer.plugins.tasks.TaskCallback
import de.pbauerochse.worklogviewer.plugins.tasks.TaskRunner
import de.pbauerochse.worklogviewer.setHref
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.settings.SettingsViewModel
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.tasks.TaskPreparer
import de.pbauerochse.worklogviewer.tasks.Tasks
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.tasks.fx.TaskProgressBar
import de.pbauerochse.worklogviewer.timerange.CustomTimerangeProvider
import de.pbauerochse.worklogviewer.timerange.TimerangeProvider
import de.pbauerochse.worklogviewer.timerange.TimerangeProviders
import de.pbauerochse.worklogviewer.timereport.TimeRange
import de.pbauerochse.worklogviewer.timereport.TimeReport
import de.pbauerochse.worklogviewer.timereport.TimeReportParameters
import de.pbauerochse.worklogviewer.trimToNull
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.version.Version
import de.pbauerochse.worklogviewer.view.grouping.Grouping
import de.pbauerochse.worklogviewer.view.grouping.GroupingFactory
import de.pbauerochse.worklogviewer.view.grouping.NoopGrouping
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDate
import java.util.*

/**
 * Java FX Controller for the main window
 */
class MainViewController : Initializable, TaskRunner, TaskPreparer {

    @FXML
    private lateinit var timerangeComboBox: ComboBox<TimerangeProvider>

    @FXML
    private lateinit var groupByCategoryComboBox: ComboBox<Grouping>

    @FXML
    private lateinit var fetchWorklogButton: Button

    @FXML
    private lateinit var exportToExcelMenuItem: MenuItem

    @FXML
    private lateinit var settingsMenuItem: MenuItem

    @FXML
    private lateinit var aboutMenuItem: MenuItem

    @FXML
    private lateinit var exitMenuItem: MenuItem

    @FXML
    private lateinit var pluginsMenu: Menu

    @FXML
    private lateinit var pluginsToolbarButtons: HBox

    @FXML
    private lateinit var taskProgressContainer: VBox

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
    private lateinit var dialog: Dialog

    override fun initialize(location: URL?, resources: ResourceBundle) {
        LOGGER.debug("Initializing main view")
        this.resources = resources
        this.settingsModel = SettingsUtil.settingsViewModel
        Tasks.registerPreparer(this)

        initializeTaskRunner()
        checkForUpdate()

        initializeDatePickers()
        initializeTimerangeComboBox()
        initializeFetchWorklogsButton()
        initializeMenuItems()
        initializePluginsMenu()
        initializeGroupingComboBox()

        // workaround to detect whether the whole form has been rendered to screen yet
        mainToolbar.sceneProperty().addListener { _, oldValue, newValue ->
            if (oldValue == null && newValue != null) {
                onFormShown()
            }
        }
    }

    private fun initializeTaskRunner() {
        waitScreenOverlay.visibleProperty().bind(Tasks.hasRunningForegroundTasks)
    }

    private fun checkForUpdate() {
        Platform.runLater {
            val versionCheckTask = CheckForUpdateTask()
            versionCheckTask.onSucceeded = EventHandler { this.addDownloadLinkToToolbarIfNeverVersionPresent(it) }
            Tasks.startBackgroundTask(versionCheckTask)
        }
    }

    private fun autoLoadLastUsedReport() {
        // auto load data if a named timerange was selected
        // and the user chose to load data at startup
        if (settingsModel.loadDataAtStartupProperty.get()) {
            LOGGER.debug("Fetching last used TimeReport upon startup")
            fetchWorklogs()
        }
    }

    private fun initializeTimerangeComboBox() {
        timerangeComboBox.apply {
            converter = TimerangeProviderStringConverter()
            items.addAll(TimerangeProviders.allTimerangeProviders)
            selectionModel.select(settingsModel.lastUsedReportTimerangeProperty.get())
            selectionModel.selectedItemProperty().addListener { _, _, selectedTimerange -> timerangeChanged(selectedTimerange) }
        }
        settingsModel.lastUsedReportTimerangeProperty.addListener { _, _, newValue -> timerangeComboBox.selectionModel.select(newValue) }
        timerangeChanged(settingsModel.lastUsedReportTimerangeProperty.value)
    }

    private fun timerangeChanged(timerangeProvider: TimerangeProvider) {
        // prepopulate start and end datepickers and remove error labels
        val timerange = timerangeProvider.buildTimeRange(startDatePicker.value, endDatePicker.value)
        startDatePicker.value = timerange.start
        endDatePicker.value = timerange.end
        settingsModel.lastUsedReportTimerangeProperty.set(timerangeProvider)
    }

    private fun initializeDatePickers() {
        // start and end datepicker are only editable if report timerange is CUSTOM
        val dateChangeListener = ChangeListener<LocalDate> { observable, _, newDate ->
            LOGGER.debug("Setting date on $observable to $newDate")
            val datePicker = (observable as SimpleObjectProperty<*>).bean as DatePicker
            if (newDate == null) {
                datePicker.styleClass.add(REQUIRED_FIELD_CLASS)
            } else {
                datePicker.styleClass.remove(REQUIRED_FIELD_CLASS)
            }
        }

        startDatePicker.disableProperty().bind(timerangeComboBox.selectionModel.selectedItemProperty().isNotEqualTo(CustomTimerangeProvider))
        startDatePicker.valueProperty().addListener(dateChangeListener)

        endDatePicker.disableProperty().bind(timerangeComboBox.selectionModel.selectedItemProperty().isNotEqualTo(CustomTimerangeProvider))
        endDatePicker.valueProperty().addListener(dateChangeListener)

        DatePickerManualEditListener.applyTo(startDatePicker)
        DatePickerManualEditListener.applyTo(endDatePicker)

        // value listener
        startDatePicker.valueProperty().addListener { _, _, newValue -> settingsModel.startDateProperty.set(newValue) }
        endDatePicker.valueProperty().addListener { _, _, newValue -> settingsModel.endDateProperty.set(newValue) }

        // set value
        if (settingsModel.lastUsedReportTimerangeProperty.get() == CustomTimerangeProvider) {
            startDatePicker.value = settingsModel.startDateProperty.get()
            endDatePicker.value = settingsModel.endDateProperty.get()
            dateChangeListener.changed(startDatePicker.valueProperty(), null, startDatePicker.value)
            dateChangeListener.changed(endDatePicker.valueProperty(), null, endDatePicker.value)
        }
    }

    private fun initializeFetchWorklogsButton() {
        // fetch worklog button click
        fetchWorklogButton.disableProperty().bind(settingsModel.hasMissingConnectionSettings)
        fetchWorklogButton.setOnAction { fetchWorklogs() }
    }

    private fun initializeMenuItems() {
        // export to excel only possible if resultTabPane is not empty and therefore seems to contain data
        exportToExcelMenuItem.disableProperty().bind(resultTabPane.selectionModel.selectedItemProperty().isNull)
        exportToExcelMenuItem.setOnAction { startExportToExcelTask() }
        settingsMenuItem.setOnAction { showSettingsDialogue() }
        exitMenuItem.setOnAction { exitWorklogViewer() }
        aboutMenuItem.setOnAction { showAboutDialogue() }
    }

    /**
     * Exports the currently visible data to an excel spreadsheet
     */
    private fun startExportToExcelTask() {
        resultTabPane.currentlyVisibleTab?.startDownloadAsExcel()
    }

    private fun showSettingsDialogue() {
        LOGGER.debug("Showing settings dialogue")

        // pass in a handler to fetch the group by categories if connection
        // parameters get set
        dialog.openDialog(
            "/fx/views/settings.fxml", DialogSpecification(
                title = getFormatted("view.settings.title"),
                modal = true
            )
        )
    }

    private fun showAboutDialogue() {
        LOGGER.debug("Showing log messages dialogue")
        dialog.openDialog("/fx/views/about.fxml", DialogSpecification(getFormatted("view.menu.help.about")))
    }

    private fun initializePluginsMenu() {
        pluginsMenu.visibleProperty().bind(settingsModel.enablePluginsProperty)
        pluginsToolbarButtons.visibleProperty().bind(settingsModel.enablePluginsProperty)
        settingsModel.enablePluginsProperty.addListener { _, _, _ -> refreshPlugins() }
        refreshPlugins()
    }

    private fun refreshPlugins() {
        Platform.runLater {
            PluginLoader.setScanForPlugins(settingsModel.enablePluginsProperty.get())

            LOGGER.debug("Removing all Plugin Actions from Menu and Toolbar")
            pluginsToolbarButtons.children.clear()
            pluginsMenu.items.clear()

            val plugins = PluginLoader.getPlugins()
            LOGGER.info("Found ${plugins.size} active Plugins")

            if (plugins.isEmpty()) {
                val noActivePluginsMenuItem = MenuItem(getFormatted("plugins.nonefound")).apply { isDisable = true }
                pluginsMenu.items.add(noActivePluginsMenuItem)
            }

            plugins
                .groupBy { it.author }
                .forEach { (author, authorPlugins) ->
                    val parent = when (authorPlugins.size) {
                        1 -> pluginsMenu
                        else -> Menu(author.name).apply { pluginsMenu.items.add(this) }
                    }

                    authorPlugins.forEach {
                        parent.items.add(PluginMenu(it) { createPluginContext() })
                        pluginsToolbarButtons.children.add(PluginToolbarActionGroup(it) { createPluginContext() })
                    }
                }
        }
    }

    private fun createPluginContext(): PluginActionContext {
        return PluginActionContextAdapter(this, dialog, getPluginState())
    }

    private fun getPluginState(): WorklogViewerState {
        return WorklogViewerStateAdapter(
            currentTimeReportProperty.get(),
            WorklogviewerUiState(
                currentlyVisibleTab = resultTabPane.currentlyVisibleTab,
                timeRange = TimeRange(startDatePicker.value, endDatePicker.value)
            )
        )
    }

    private fun initializeGroupingComboBox() {
        groupByCategoryComboBox.apply {
            disableProperty().bind(currentTimeReportProperty.isNull)
            converter = GroupingComboBoxConverter(groupByCategoryComboBox)
            selectionModel.selectedItemProperty().addListener { _, _, groupByCategory ->
                groupByCategory?.let {
                    settingsModel.lastUsedGroupByCategoryIdProperty.set(groupByCategory.id)
                    displayWorklogResult()
                }
            }
        }

        currentTimeReportProperty.addListener { _, _, newTimeReport ->
            newTimeReport?.let { report ->
                val allGroupings = GroupingFactory.getAvailableGroupings(report)
                val selectedGrouping = getSelectedGrouping(allGroupings)
                groupByCategoryComboBox.items.clear()
                groupByCategoryComboBox.items.addAll(allGroupings)
                groupByCategoryComboBox.selectionModel.select(selectedGrouping)
            }
        }
    }

    private fun getSelectedGrouping(allGroupings: List<Grouping>): Grouping {
        val groupingFromStoredSetting = settingsModel.lastUsedGroupByCategoryIdProperty.get()?.let { lastUsedGroupingId ->
            allGroupings.find { it.id == lastUsedGroupingId }
        }
        return groupingFromStoredSetting ?: NoopGrouping
    }

    private fun addDownloadLinkToToolbarIfNeverVersionPresent(event: WorkerStateEvent) {
        (event.source as CheckForUpdateTask).value?.let {
            val currentVersion = Version.fromVersionString(resources.getString("release.version"))
            val mostRecentVersion = Version.fromVersionString(it.version)

            LOGGER.debug("Most recent github version is {}, this version is {}", mostRecentVersion, currentVersion)
            if (mostRecentVersion.isNewerThan(currentVersion)) {
                val link = Hyperlink(getFormatted("worker.updatecheck.available", mostRecentVersion.toString()))
                link.setHref(it.url)
                mainToolbar.items.add(link)
            }
        }
    }

    private fun onFormShown() {
        LOGGER.debug("MainForm shown")
        this.dialog = Dialog(mainToolbar.scene)
        setupKeyboardShortcuts()

        if (settingsModel.hasMissingConnectionSettings.get()) {
            LOGGER.info("No YouTrack connection settings defined yet. Opening settings dialogue")
            showSettingsDialogue()
        } else {
            autoLoadLastUsedReport()
        }
    }

    private fun setupKeyboardShortcuts() {
        mainToolbar.scene.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            when {
                matches(settingsModel.fetchWorklogsKeyboardCombination, event) -> fetchWorklogs()
                matches(settingsModel.showIssueSearchKeyboardCombination, event) -> showAddWorkitemDialog()
                matches(settingsModel.showSettingsKeyboardCombination, event) -> showSettingsDialogue()
                matches(settingsModel.toggleStatisticsKeyboardCombination, event) -> settingsModel.showStatisticsProperty.set(!settingsModel.showStatisticsProperty.get())
                matches(settingsModel.exitWorklogViewerKeyboardCombination, event) -> exitWorklogViewer()
            }
        }
    }

    private fun matches(keyCombinationProperty: SimpleObjectProperty<KeyCombination>, event: KeyEvent): Boolean {
        return keyCombinationProperty.value?.match(event) ?: false
    }

    /**
     * Fetches the worklogs for the currently defined settings from YouTrack
     */
    private fun fetchWorklogs() {
        LOGGER.debug("Fetch worklogs clicked for timerange ${timerangeComboBox.selectionModel.selectedItem}")
        val selectedStartDate = startDatePicker.value
        val selectedEndDate = endDatePicker.value

        val timeRange = TimeRange(selectedStartDate, selectedEndDate)
        val parameters = TimeReportParameters(timeRange)

        val task = FetchTimereportTask(DataSources.activeDataSource!!, parameters)
        task.setOnSucceeded { event -> currentTimeReportProperty.value = event.source.value as TimeReport }
        Tasks.startTask(task)
    }

    private fun showAddWorkitemDialog() {
        LOGGER.debug("Showing AddWorkitem Dialog")
        resultTabPane.showSearchTab()
    }

    private fun displayWorklogResult() {
        LOGGER.info("Presenting TimeReport to the user")
        val timeReport = currentTimeReportProperty.value!!
        val grouping = groupByCategoryComboBox.selectionModel.selectedItem
        resultTabPane.update(timeReport, grouping)
    }

    private fun exitWorklogViewer() {
        WorklogViewer.instance.requestShutdown()
    }

    override fun <T> start(task: PluginTask<T>, callback: TaskCallback<T>?) {
        val wrapper = object : WorklogViewerTask<T?>(task.label) {
            override fun start(progress: Progress): T? = task.run(progress)
        }
        wrapper.setOnSucceeded { callback?.invoke(it.source.value as T?) }
        Tasks.startTask(wrapper)
    }

    override fun <T> prepareTaskForExecution(task: WorklogViewerTask<T>): WorklogViewerTask<T> {
        return TaskInitializer.initialize(taskProgressContainer, task)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MainViewController::class.java)
        private const val REQUIRED_FIELD_CLASS = "required"
    }

    /**
     * Wrapper for a WorklogViewerTasks that handles
     * the ui blocking and displays a progress bar
     * for the task on the main application window
     */
    internal object TaskInitializer {

        internal fun <T> initialize(taskProgressContainer: Pane, task: WorklogViewerTask<T>) : WorklogViewerTask<T> {
            val progressbar = TaskProgressBar(task, true)
            task.stateProperty().addListener(progressbar)
            bindOnRunning(task, progressbar, taskProgressContainer)
            bindOnSucceeded(task, progressbar)
            bindOnFailed(task, progressbar)
            return task
        }

        private fun bindOnRunning(task : WorklogViewerTask<*>, progressbar: TaskProgressBar, progressbarContainer : Pane) {
            val initialHandler = task.onRunning
            task.setOnRunning {
                progressbarContainer.children.add(progressbar)
                progressbar.progressText.textProperty().bind(task.messageProperty())
                progressbar.progressBar.progressProperty().bind(task.progressProperty())

                initialHandler?.handle(it)
            }
        }

        private fun bindOnSucceeded(task: WorklogViewerTask<*>, progressbar: TaskProgressBar) {
            val initialHandler = task.onSucceeded
            task.setOnSucceeded {
                progressbar.progressText.textProperty().unbind()
                progressbar.progressBar.progressProperty().unbind()
                initialHandler?.handle(it)
            }
        }

        private fun bindOnFailed(task: WorklogViewerTask<*>, progressbar: TaskProgressBar) {
            val initialHandler = task.onFailed
            task.setOnFailed {
                progressbar.progressText.textProperty().unbind()
                progressbar.progressBar.progressProperty().unbind()

                progressbar.progressText.text = getErrorMessage(it)
                progressbar.progressBar.progress = 1.0

                initialHandler?.handle(it)
            }
        }

        private fun getErrorMessage(event: WorkerStateEvent): String {
            return event.source.exception?.message?.trimToNull() ?: getFormatted("exceptions.main.worker.unknown")
        }
    }
}
