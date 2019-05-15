package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.report.Issue
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import java.net.URL
import java.time.LocalDate
import java.util.*

/**
 * Controller for the view to add a work item
 * to an issue
 */
class AddWorkItemController : Initializable {

    val issueProperty : ObjectProperty<Issue?> = SimpleObjectProperty()
    val dateProperty : ObjectProperty<LocalDate?> = SimpleObjectProperty()

    @FXML
    private lateinit var saveButton : Button

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        saveButton.text = "Rakete"
    }

    fun closeDialog(actionEvent: ActionEvent) {

    }

    fun saveNewWorkItem(actionEvent: ActionEvent) {

    }

}