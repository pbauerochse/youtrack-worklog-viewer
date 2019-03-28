package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.WorklogViewer
import de.pbauerochse.worklogviewer.connector.GroupByParameter
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator
import de.pbauerochse.worklogviewer.domain.ReportTimerange
import de.pbauerochse.worklogviewer.domain.timerangeprovider.TimerangeProviderFactory
import de.pbauerochse.worklogviewer.fx.components.NoSelectionGroupByParameter
import de.pbauerochse.worklogviewer.fx.components.plugins.PluginMenu
import de.pbauerochse.worklogviewer.fx.components.plugins.PluginTask
import de.pbauerochse.worklogviewer.fx.components.plugins.PluginToolbarActionGroup
import de.pbauerochse.worklogviewer.fx.components.tabs.TimeReportResultTabbedPane
import de.pbauerochse.worklogviewer.fx.converter.GroupByCategoryStringConverter
import de.pbauerochse.worklogviewer.fx.converter.ReportTimerangeStringConverter
import de.pbauerochse.worklogviewer.fx.tasks.CheckForUpdateTask
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportTask
import de.pbauerochse.worklogviewer.fx.tasks.GetGroupByCategoriesTask
import de.pbauerochse.worklogviewer.fx.tasks.TaskRunner
import de.pbauerochse.worklogviewer.fx.theme.ThemeChangeListener
import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.isNoSelection
import de.pbauerochse.worklogviewer.plugin.FileChooserSpec
import de.pbauerochse.worklogviewer.plugin.PluginActionContext
import de.pbauerochse.worklogviewer.plugin.PopupSpecification
import de.pbauerochse.worklogviewer.plugin.TabContext
import de.pbauerochse.worklogviewer.plugins.PluginLoader
import de.pbauerochse.worklogviewer.report.TimeRange
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import de.pbauerochse.worklogviewer.setHref
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.settings.SettingsViewModel
import de.pbauerochse.worklogviewer.tasks.AsyncTask
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.version.Version
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
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
import java.util.concurrent.TimeUnit

/**
 * Java FX Controller for the main window
 */
class MainViewController : Initializable, PluginActionContext {

    @FXML
    private lateinit var timerangeComboBox: ComboBox<ReportTimerange>

    @FXML
    private lateinit var groupByCategoryComboBox: ComboBox<GroupByParameter>

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

    private lateinit var taskRunner: TaskRunner

    private lateinit var resources: ResourceBundle
    private lateinit var settingsModel: SettingsViewModel

    override fun initialize(location: URL?, resources: ResourceBundle) {
        LOGGER.debug("Initializing main view")
        this.resources = resources
        this.settingsModel = SettingsUtil.settingsViewModel
        this.taskRunner = TaskRunner(taskProgressContainer, waitScreenOverlay)

        checkForUpdate()

        initializeTimerangeComboBox()
        initializeGroupByComboBox()
        initializeDatePickers()
        initializeFetchWorklogsButton()
        initializeMenuItems()
        initializePluginsMenu()

        // workaround to detect whether the whole form has been rendered to screen yet
        mainToolbar.sceneProperty().addListener { _, oldValue, newValue ->
            if (oldValue == null && newValue != null) {
                onFormShown()
            }
        }
    }

    override val http: Http
        get() = Http(settingsModel.settings.youTrackConnectionSettings)

    override val connector: YouTrackConnector?
        get() = YouTrackConnectorLocator.getActiveConnector()

    override var currentTimeReport: TimeReport? = null

    override val currentlyVisibleIssues: TabContext?
        get() = resultTabPane.currentlyVisibleTab.currentData?.let {
            TabContext(it.reportParameters, it.issues)
        }

    override fun <T> triggerTask(task: AsyncTask<T>): T? {
        val pluginTask = PluginTask(task)
        val result = taskRunner.startTask(pluginTask)
        return result.get(30, TimeUnit.SECONDS)
    }

    override fun showInPopup(fxmlUrl: URL, specs: PopupSpecification) {
        openDialogue(fxmlUrl.toExternalForm(), specs.title, specs.modal, specs.onClose)
    }

    override fun showSaveFileDialog(spec: FileChooserSpec): File? {
        return fileChooser(spec).showSaveDialog(resultTabPane.scene.window)
    }

    override fun showOpenFileDialog(spec: FileChooserSpec): File? {
        return fileChooser(spec).showOpenDialog(resultTabPane.scene.window)
    }

    private fun fileChooser(spec: FileChooserSpec): FileChooser {
        return FileChooser().apply {
            title = spec.title
            initialFileName = spec.initialFileName
            selectedExtensionFilter = spec.fileType?.let {
                FileChooser.ExtensionFilter(it.description, it.extension)
            }
        }
    }

    private fun initializeTimerangeComboBox() {
        timerangeComboBox.apply {
            converter = ReportTimerangeStringConverter()
            items.addAll(*ReportTimerange.values())
            selectionModel.select(settingsModel.lastUsedReportTimerangeProperty.get())
            selectionModel.selectedItemProperty().addListener { _, _, selectedTimerange -> timerangeChanged(selectedTimerange) }
        }

        settingsModel.lastUsedReportTimerangeProperty.addListener { _, _, newValue -> timerangeComboBox.selectionModel.select(newValue) }
        timerangeChanged(settingsModel.lastUsedReportTimerangeProperty.value)
    }

    private fun timerangeChanged(newValue: ReportTimerange) {
        // prepopulate start and end datepickers and remove error labels
        val timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(newValue, startDatePicker.value, endDatePicker.value)
        startDatePicker.value = timerangeProvider.startDate
        endDatePicker.value = timerangeProvider.endDate
        settingsModel.lastUsedReportTimerangeProperty.set(newValue)
    }

    private fun initializeGroupByComboBox() {
        groupByCategoryComboBox.apply {
            disableProperty().bind(groupByCategoryComboBox.itemsProperty().isNull)
            converter = GroupByCategoryStringConverter(groupByCategoryComboBox)
        }

        // load group by criteria when connection parameters are present
        if (!settingsModel.hasMissingConnectionSettings.get()) {
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
            val categoryList = worker.value as List<GroupByParameter>
            LOGGER.info("{} succeeded with {} GroupByCategories", task.title, categoryList.size)

            groupByCategoryComboBox.items.add(NoSelectionGroupByParameter())
            groupByCategoryComboBox.items.addAll(categoryList.sortedBy { it.getLabel() })
            groupByCategoryComboBox.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
                val lastUsed = newValue?.id
                settingsModel.lastUsedGroupByCategoryIdProperty.value = lastUsed
            }

            val lastUsedGroupById = settingsModel.lastUsedGroupByCategoryIdProperty
            var selectedItemIndex = 0

            for (i in 0 until groupByCategoryComboBox.items.size) {
                if (StringUtils.equals(groupByCategoryComboBox.items[i].id, lastUsedGroupById.get())) {
                    selectedItemIndex = i
                    break
                }
            }

            groupByCategoryComboBox.selectionModel.select(selectedItemIndex)
            autoLoadLastUsedReport()
        }
        task.setOnFailed { autoLoadLastUsedReport() }

        taskRunner.startTask(task)
    }

    private fun autoLoadLastUsedReport() {
        // auto load data if a named timerange was selected
        // and the user chose to load data at startup
        if (timerangeComboBox.selectionModel.selectedItem != ReportTimerange.CUSTOM && settingsModel.loadDataAtStartupProperty.get()) {
            LOGGER.debug("loadDataAtStartup set. Loading report for {}", timerangeComboBox.selectionModel.selectedItem.name)
            fetchWorklogButton.fire()
        }
    }

    private fun initializeDatePickers() {
        // start and end datepicker are only editable if report timerange is CUSTOM
        val dateChangeListener = ChangeListener<LocalDate> { observable, _, newDate ->
            LOGGER.info("Setting start date to {} on {}", newDate, observable)
            val datePicker = (observable as SimpleObjectProperty<*>).bean as DatePicker
            if (newDate == null) {
                datePicker.styleClass.add(REQUIRED_FIELD_CLASS)
            } else {
                datePicker.styleClass.remove(REQUIRED_FIELD_CLASS)
            }
        }

        startDatePicker.disableProperty().bind(timerangeComboBox.selectionModel.selectedItemProperty().isNotEqualTo(ReportTimerange.CUSTOM))
        startDatePicker.valueProperty().addListener(dateChangeListener)

        endDatePicker.disableProperty().bind(timerangeComboBox.selectionModel.selectedItemProperty().isNotEqualTo(ReportTimerange.CUSTOM))
        endDatePicker.valueProperty().addListener(dateChangeListener)

        // value listener
        startDatePicker.valueProperty().addListener { _, _, newValue -> settingsModel.startDateProperty.set(newValue) }
        endDatePicker.valueProperty().addListener { _, _, newValue -> settingsModel.endDateProperty.set(newValue) }

        // set value
        if (settingsModel.lastUsedReportTimerangeProperty.get() == ReportTimerange.CUSTOM) {
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

        // menu items click actions
        exportToExcelMenuItem.setOnAction { startExportToExcelTask() }

        settingsMenuItem.setOnAction { showSettingsDialogue() }
        exitMenuItem.setOnAction { WorklogViewer.getInstance().requestShutdown() }
        logMessagesMenuItem.setOnAction { showLogMessagesDialogue() }
        aboutMenuItem.setOnAction { showAboutDialogue() }
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

        // TODO Exception handling from plugins

        val plugins = PluginLoader.getPlugins()
        LOGGER.debug("Found ${plugins.size} active Plugins")

        if (plugins.isEmpty()) {
            val noActivePluginsMenuItem = MenuItem(FormattingUtil.getFormatted("plugins.nonefound")).apply { isDisable = true }
            pluginsMenu.items.add(noActivePluginsMenuItem)
        }

        plugins
            .groupBy { it.vendor }
            .forEach { vendor, vendorPlugins ->
                val parent = when (vendorPlugins.size) {
                    1 -> pluginsMenu
                    else -> Menu(vendor.name).apply { pluginsMenu.items.add(this) }
                }

                vendorPlugins.forEach {
                    parent.items.add(PluginMenu(it, this))
                    pluginsToolbarButtons.children.add(PluginToolbarActionGroup(it, this))
                }
            }
    }

    private fun checkForUpdate() {
        val versionCheckTask = CheckForUpdateTask()
        versionCheckTask.onSucceeded = EventHandler { this.addDownloadLinkToToolbarIfNeverVersionPresent(it) }
        taskRunner.startTask(versionCheckTask)
    }

    private fun addDownloadLinkToToolbarIfNeverVersionPresent(event: WorkerStateEvent) {
        val gitHubVersionOptional = (event.source as CheckForUpdateTask).value
        gitHubVersionOptional.ifPresent { gitHubVersion ->
            val currentVersion = Version.fromVersionString(resources.getString("release.version"))
            val mostRecentVersion = Version.fromVersionString(gitHubVersion.version)

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
        if (settingsModel.hasMissingConnectionSettings.get()) {
            LOGGER.info("No YouTrack connection settings defined yet. Opening settings dialogue")
            showSettingsDialogue()
        }
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

    /**
     * Fetches the worklogs for the currently defined settings from YouTrack
     */
    private fun fetchWorklogs() {
        val timerange = timerangeComboBox.selectionModel.selectedItem
        LOGGER.debug("Fetch worklogs clicked for timerange {}", timerange.toString())

        val selectedStartDate = startDatePicker.value
        val selectedEndDate = endDatePicker.value

        val groupByCategory = groupByCategoryComboBox.selectionModel.selectedItem

        val timeRange = TimeRange(selectedStartDate, selectedEndDate)
        val parameters = TimeReportParameters(timeRange, groupByCategory?.takeIf { it.isNoSelection().not() })

        val task = FetchTimereportTask(parameters)
        task.setOnSucceeded { event -> displayWorklogResult(event.source.value as TimeReport) }
        taskRunner.startTask(task)
    }

    private fun displayWorklogResult(timeReport: TimeReport) {
        LOGGER.info("Presenting TimeReport to the user")
        resultTabPane.update(timeReport)
        currentTimeReport = timeReport
    }

    private fun showSettingsDialogue() {
        LOGGER.debug("Showing settings dialogue")

        // pass in a handler to fetch the group by categories if connection
        // parameters get set
        openDialogue("/fx/views/settings.fxml", "view.settings.title", true) {
            if (!settingsModel.hasMissingConnectionSettings.get() && groupByCategoryComboBox.items.size == 0) {
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
            scene.stylesheets.add(settingsViewModel.themeProperty.get().stylesheet)

            val themeChangeListener = ThemeChangeListener(scene)
            settingsViewModel.themeProperty.addListener(themeChangeListener)

            val stage = Stage()
            stage.initOwner(mainToolbar.scene.window)

            if (modal) {
                stage.initStyle(StageStyle.UTILITY)
                stage.initModality(Modality.APPLICATION_MODAL)
                stage.isResizable = false
            }

            stage.title = getFormatted(titleResourceKey)
            stage.scene = scene

            if (onCloseCallback != null) {
                stage.setOnCloseRequest {
                    LOGGER.debug("View {} got close request. Notifying callback", view)
                    settingsViewModel.themeProperty.removeListener(themeChangeListener)
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

    }
}
