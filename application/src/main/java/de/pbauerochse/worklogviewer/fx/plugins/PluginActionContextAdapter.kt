package de.pbauerochse.worklogviewer.fx.plugins

import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource
import de.pbauerochse.worklogviewer.plugins.actions.PluginActionContext
import de.pbauerochse.worklogviewer.plugins.dialog.WorklogViewerDialog
import de.pbauerochse.worklogviewer.plugins.formatter.YouTrackWorktimeFormatter
import de.pbauerochse.worklogviewer.plugins.state.WorklogViewerState
import de.pbauerochse.worklogviewer.plugins.tasks.TaskRunner
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import de.pbauerochse.worklogviewer.util.WorklogTimeFormatter

class PluginActionContextAdapter(
    override val taskRunner: TaskRunner,
    override val dialog: WorklogViewerDialog,
    override val state: WorklogViewerState
) : PluginActionContext {

    override val connector: TimeTrackingDataSource
        get() = DataSources.activeDataSource ?: throw ExceptionUtil.getIllegalStateException("exceptions.notsetyet", TimeTrackingDataSource::class.java.simpleName)

    override val timesFormatter: YouTrackWorktimeFormatter
        get() = WorklogTimeFormatter(SettingsUtil.settingsViewModel.workhoursProperty.value)

}