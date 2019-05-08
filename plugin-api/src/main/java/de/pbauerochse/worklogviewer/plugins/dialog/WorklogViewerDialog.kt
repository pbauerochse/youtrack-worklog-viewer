package de.pbauerochse.worklogviewer.plugins.dialog

import java.io.File
import java.net.URL

interface WorklogViewerDialog {
    fun showFxml(fxmlUrl : URL, specs : DialogSpecification)
    fun showSaveFileDialog(specification : FileChooserSpecification) : File?
    fun showOpenFileDialog(specification : FileChooserSpecification) : File?
}