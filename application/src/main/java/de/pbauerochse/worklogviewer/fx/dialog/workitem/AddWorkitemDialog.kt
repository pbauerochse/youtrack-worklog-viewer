package de.pbauerochse.worklogviewer.fx.dialog.workitem

import de.pbauerochse.worklogviewer.fx.AddWorkItemController
import de.pbauerochse.worklogviewer.fx.components.treetable.columns.context.IssueCellContextMenu
import de.pbauerochse.worklogviewer.fx.dialog.Dialog
import de.pbauerochse.worklogviewer.plugins.dialog.DialogSpecification
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import java.time.LocalDate

/**
 * Opens a dialog to add an work item
 */
object AddWorkitemDialog {
    fun show(dialog : Dialog) = show(dialog, LocalDate.now())
    fun show(scene: Scene, date : LocalDate? = null, issue : Issue? = null) = show(Dialog(scene), date, issue)
    fun show(dialog : Dialog, date : LocalDate? = null, issue : Issue? = null) {
        val loader = FXMLLoader(IssueCellContextMenu::class.java.getResource("/fx/views/add-workitem.fxml"), FormattingUtil.RESOURCE_BUNDLE)
        val root = loader.load<Parent>()
        val controller = loader.getController<AddWorkItemController>()
        controller.issueProperty.set(issue?.id)
        controller.dateProperty.set(date)

        dialog.openDialog(root, DialogSpecification(FormattingUtil.getFormatted("dialog.addworkitem.title"), true))
    }
}