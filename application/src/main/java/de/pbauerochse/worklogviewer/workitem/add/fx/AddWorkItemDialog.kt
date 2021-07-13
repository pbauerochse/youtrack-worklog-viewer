package de.pbauerochse.worklogviewer.workitem.add.fx

import de.pbauerochse.worklogviewer.fx.dialog.Dialog
import de.pbauerochse.worklogviewer.plugins.dialog.DialogSpecification
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import java.time.LocalDate

/**
 * Opens a dialog to add an work item
 */
object AddWorkItemDialog {
    fun show(scene: Scene, date: LocalDate, issue: Issue? = null) = show(Dialog(scene), date, issue)
    private fun show(dialog: Dialog, date: LocalDate, issue: Issue? = null) {
        val loader = FXMLLoader(AddWorkItemDialog::class.java.getResource("/fx/views/add-workitem.fxml"), FormattingUtil.RESOURCE_BUNDLE)
        val root = loader.load<Parent>()

        val controller = loader.getController<AddWorkItemController>()
        controller.model.forIssueAtDate(issue, date)

        dialog.openDialog(root, DialogSpecification(FormattingUtil.getFormatted("dialog.addworkitem.title"), true))
    }
}
