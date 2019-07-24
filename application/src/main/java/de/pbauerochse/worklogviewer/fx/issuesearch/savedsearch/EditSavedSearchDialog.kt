package de.pbauerochse.worklogviewer.fx.issuesearch.savedsearch

import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.DialogPane
import javafx.scene.control.Label
import javafx.stage.Modality

class EditSavedSearchDialog : Dialog<SavedSearch>() {

    init {
        val dialogPane = DialogPane().apply {
            buttonTypes.setAll(listOf(ButtonType.OK, ButtonType.CANCEL))
            content = Label("TODO load from saved-search.fxml")
        }
        // TODO styles and positioning
        // TODO main app backdrop
        this.dialogPane = dialogPane
        this.initModality(Modality.APPLICATION_MODAL)
//        this.owner =
    }

}