package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.WorklogViewer
import de.pbauerochse.worklogviewer.fx.components.plugins.PluginMenu
import de.pbauerochse.worklogviewer.fx.components.plugins.PluginToolbarActionGroup
import de.pbauerochse.worklogviewer.fx.components.tabs.TimeReportResultTabbedPane
import de.pbauerochse.worklogviewer.fx.converter.GroupingComboBoxConverter
import de.pbauerochse.worklogviewer.fx.converter.TimerangeProviderStringConverter
import de.pbauerochse.worklogviewer.fx.dialog.Dialog
import de.pbauerochse.worklogviewer.fx.plugins.PluginActionContextAdapter
import de.pbauerochse.worklogviewer.fx.plugins.WorklogViewerStateAdapter
import de.pbauerochse.worklogviewer.fx.tasks.CheckForUpdateTask
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportTask
import de.pbauerochse.worklogviewer.fx.tasks.TaskRunnerImpl
import de.pbauerochse.worklogviewer.logging.ProcessPendingLogsService
import de.pbauerochse.worklogviewer.plugins.PluginLoader
import de.pbauerochse.worklogviewer.plugins.actions.PluginActionContext
import de.pbauerochse.worklogviewer.plugins.dialog.DialogSpecification
import de.pbauerochse.worklogviewer.plugins.state.WorklogViewerState
import de.pbauerochse.worklogviewer.report.TimeRange
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import de.pbauerochse.worklogviewer.setHref
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.settings.SettingsViewModel
import de.pbauerochse.worklogviewer.timerange.CustomTimerangeProvider
import de.pbauerochse.worklogviewer.timerange.TimerangeProvider
import de.pbauerochse.worklogviewer.timerange.TimerangeProviders
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.version.Version
import de.pbauerochse.worklogviewer.view.grouping.Grouping
import de.pbauerochse.worklogviewer.view.grouping.GroupingFactory
import de.pbauerochse.worklogviewer.view.grouping.NoopGrouping
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDate
import java.util.*

/**
 * Java FX Controller for the main window
 */
class MainViewController : Initializable {

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
    private lateinit var logMessagesMenuItem: MenuItem

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

    private val currentTimeReportProperty = SimpleObjectProperty<TimeReport?>(null)

    private lateinit var taskRunner : TaskRunnerImpl

    private lateinit var resources: ResourceBundle
    private lateinit var settingsModel: SettingsViewModel
    private lateinit var dialog : Dialog

    override fun initialize(location: URL?, resources: ResourceBundle) {
        LOGGER.debug("Initializing main view")
        this.resources = resources
        this.settingsModel = SettingsUtil.settingsViewModel
        this.taskRunner = TaskRunnerImpl(taskProgressContainer, waitScreenOverlay)

        startLogViewUpdaterTask()
        checkForUpdate()
        autoLoadLastUsedReport()

        initializeTimerangeComboBox()
        initializeDatePickers()
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

    private fun startLogViewUpdaterTask() {
        val service = ProcessPendingLogsService()
        service.delay = Duration.millis(1.0)
        service.period = Duration.seconds(1.0)
        service.restartOnFailure = true
        taskRunner.startService(service)
    }

    private fun checkForUpdate() {
        val versionCheckTask = CheckForUpdateTask()
        versionCheckTask.onSucceeded = EventHandler { this.addDownloadLinkToToolbarIfNeverVersionPresent(it) }
        taskRunner.startTask(versionCheckTask)
    }

    private fun autoLoadLastUsedReport() {
        // auto load data if a named timerange was selected
        // and the user chose to load data at startup
        if (settingsModel.loadDataAtStartupProperty.get()) {
            LOGGER.debug("Fetching last used TimeReport upon startup")
            fetchWorklogButton.fire()
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
            LOGGER.info("Setting date on $observable to $newDate")
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
        logMessagesMenuItem.setOnAction { showLogMessagesDialogue() }
        aboutMenuItem.setOnAction { showAboutDialogue() }
    }

    /**
     * Exports the currently visible data to an excel spreadsheet
     */
    private fun startExportToExcelTask() {
        val tab = resultTabPane.currentlyVisibleTab
        tab.getDownloadAsExcelTask()?.let {
            taskRunner.startTask(it)
        }
    }

    private fun showSettingsDialogue() {
        LOGGER.debug("Showing settings dialogue")

        // pass in a handler to fetch the group by categories if connection
        // parameters get set
        dialog.openDialog("/fx/views/settings.fxml", DialogSpecification(
            title = getFormatted("view.settings.title"),
            modal = true
        ))
    }

    private fun showLogMessagesDialogue() {
        LOGGER.debug("Showing log messages dialogue")
        dialog.openDialog("/fx/views/logMessagesView.fxml", DialogSpecification(getFormatted("view.menu.help.logs")))
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

    private fun createPluginContext(): PluginActionContext {
        return PluginActionContextAdapter(taskRunner, dialog, getPluginState())
    }

    private fun getPluginState(): WorklogViewerState {
        return WorklogViewerStateAdapter(
            currentTimeReportProperty.get(),
            resultTabPane.currentlyVisibleTab
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

        currentTimeReportProperty.apply {
            addListener { _, _, newTimeReport ->
                newTimeReport?.let { report ->
                    val allGroupings = GroupingFactory.getAvailableGroupings(report)
                    val selectedGrouping = getSelectedGrouping(allGroupings)
                    groupByCategoryComboBox.items.clear()
                    groupByCategoryComboBox.items.addAll(allGroupings)
                    groupByCategoryComboBox.selectionModel.select(selectedGrouping)
                }
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
        }
    }

    private fun setupKeyboardShortcuts() {
        mainToolbar.scene.accelerators[KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN)] = Runnable { fetchWorklogs() }
        mainToolbar.scene.accelerators[KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN)] = Runnable { fetchWorklogs() }
        mainToolbar.scene.accelerators[KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN)] = Runnable { showSettingsDialogue() }
        mainToolbar.scene.accelerators[KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN)] = Runnable { exitWorklogViewer() }
    }

    /**
     * Fetches the worklogs for the currently defined settings from YouTrack
     */
    private fun fetchWorklogs() {
        val timerange = timerangeComboBox.selectionModel.selectedItem
        LOGGER.debug("Fetch worklogs clicked for timerange {}", timerange.toString())

        val selectedStartDate = startDatePicker.value
        val selectedEndDate = endDatePicker.value

        val timeRange = TimeRange(selectedStartDate, selectedEndDate)
        val parameters = TimeReportParameters(timeRange)

        val task = FetchTimereportTask(parameters)
        task.setOnSucceeded { event -> currentTimeReportProperty.value = event.source.value as TimeReport }
        taskRunner.startTask(task)
    }

    private fun displayWorklogResult() {
        LOGGER.info("Presenting TimeReport to the user")
        val timeReport = currentTimeReportProperty.value!!
        val grouping = groupByCategoryComboBox.selectionModel.selectedItem
        resultTabPane.update(timeReport, grouping)
    }

    private fun exitWorklogViewer() {
        WorklogViewer.getInstance().requestShutdown()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(MainViewController::class.java)
        private const val REQUIRED_FIELD_CLASS = "required"
    }
}
