package de.pbauerochse.worklogviewer.plugins.actions

import de.pbauerochse.worklogviewer.plugins.state.WorklogViewerState
import de.pbauerochse.worklogviewer.plugins.tools.WorklogViewerTools

interface PluginActionContext {
    val state: WorklogViewerState
    val tools: WorklogViewerTools
}