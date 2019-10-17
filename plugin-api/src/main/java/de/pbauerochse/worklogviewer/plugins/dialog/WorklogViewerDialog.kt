package de.pbauerochse.worklogviewer.plugins.dialog

import java.net.URL

interface WorklogViewerDialog {
    fun showFxml(fxmlUrl: URL, specs: DialogSpecification)
    fun showSaveFileDialog(specification: FileChooserSpecification, callback: DialogCallback)
    fun showOpenFileDialog(specification: FileChooserSpecification, callback: DialogCallback)
}