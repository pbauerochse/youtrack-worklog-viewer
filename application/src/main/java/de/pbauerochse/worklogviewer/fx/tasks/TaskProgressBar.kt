package de.pbauerochse.worklogviewer.fx.tasks

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.ProgressBar
import javafx.scene.layout.StackPane
import javafx.scene.text.Text

/**
 * Component that groups a Progressbar, as well as
 * a label for the Task to be completed, and a
 * progress message label
 */
class TaskProgressBar : StackPane() {

    @FXML
    lateinit var progressBar: ProgressBar
        private set

    @FXML
    lateinit var taskName: Text
        private set

    @FXML
    lateinit var progressText: Text
        private set

    init {
        val loader = FXMLLoader(this::class.java.getResource("/fx/components/task-progress-bar.fxml"))
        loader.setRoot(this)
        loader.setController(this)
        loader.load<Parent>()
    }
}