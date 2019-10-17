package de.pbauerochse.worklogviewer.fx.dialog.workitem

import de.pbauerochse.worklogviewer.fx.AddWorkItemController
import de.pbauerochse.worklogviewer.fx.dialog.Dialog
import de.pbauerochse.worklogviewer.plugins.dialog.DialogSpecification
import de.pbauerochse.worklogviewer.report.MinimalIssue
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import java.time.LocalDate

/**
 * Opens a dialog to add an work item
 */
object WorkitemDialogs {
    fun show(dialog: Dialog) = show(dialog, LocalDate.now())
    fun show(scene: Scene, date: LocalDate? = null, issue: MinimalIssue? = null) = show(Dialog(scene), date, issue)
    private fun show(dialog: Dialog, date: LocalDate? = null, issue: MinimalIssue? = null) {
        val loader = FXMLLoader(WorkitemDialogs::class.java.getResource("/fx/views/add-workitem.fxml"), FormattingUtil.RESOURCE_BUNDLE)
        val root = loader.load<Parent>()
        val controller = loader.getController<AddWorkItemController>()
        controller.issueProperty.set(issue?.id)
        controller.dateProperty.set(date)

        dialog.openDialog(root, DialogSpecification(FormattingUtil.getFormatted("dialog.addworkitem.title"), true))
    }
}
