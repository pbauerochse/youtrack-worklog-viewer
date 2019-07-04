package de.pbauerochse.worklogviewer.fx.issuesearch.details

import de.pbauerochse.worklogviewer.report.WorklogItem
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.WorklogTimeFormatter
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import java.time.LocalDate

/**
 * Displays the formatted date of a WorklogItem
 */
class WorklogDateColumn : TableColumn<WorklogItem, LocalDate>("DATUM") {
    init {
        cellValueFactory = PropertyValueFactory<WorklogItem, LocalDate>("date")
        cellFactory = WorklogDateCell.CELL_FACTORY
        isSortable = true
    }
}
private class WorklogDateCell : TableCell<WorklogItem, LocalDate>() {
    override fun updateItem(item: LocalDate?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.let { FormattingUtil.formatDate(it) }
    }

    companion object {
        val CELL_FACTORY = Callback<TableColumn<WorklogItem, LocalDate>?, TableCell<WorklogItem, LocalDate>?> { WorklogDateCell() }
    }
}

/**
 * Displays the type of the worklog item
 */
class WorklogTypeColumn : TableColumn<WorklogItem, String?>("TYPE") {
    init {
        cellValueFactory = PropertyValueFactory<WorklogItem, String?>("workType")
        isSortable = true
    }
}

/**
 * Displays the description of the WorklogItem
 */
class WorklogDescriptionColumn : TableColumn<WorklogItem, String?>("DESCRIPTION") {
    init {
        cellValueFactory = PropertyValueFactory<WorklogItem, String?>("description")
        isSortable = true
    }
}

/**
 * Displays the formatted duration of the WorklogItem
 */
class WorklogDurationColumn : TableColumn<WorklogItem, Long>("DURATION") {
    init {
        cellValueFactory = PropertyValueFactory<WorklogItem, Long>("durationInMinutes")
        cellFactory = WorklogDurationCell.CELL_FACTORY
        isSortable = true
    }
}
private class WorklogDurationCell : TableCell<WorklogItem, Long>() {
    override fun updateItem(item: Long?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.let {
            WorklogTimeFormatter(SettingsUtil.settingsViewModel.workhoursProperty.value).getFormatted(it)
        }
    }

    companion object {
        val CELL_FACTORY = Callback<TableColumn<WorklogItem, Long>?, TableCell<WorklogItem, Long>?> { WorklogDurationCell() }
    }
}

class WorklogActionsColumn : TableColumn<WorklogItem, WorklogItem>() {
    init {
        cellFactory = WorklogActionsCell.CELL_FACTORY
        isSortable = false
    }
}
private class WorklogActionsCell : TableCell<WorklogItem, WorklogItem>() {
    override fun updateItem(item: WorklogItem?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.let { "TODO" }
    }

    companion object {
        val CELL_FACTORY = Callback<TableColumn<WorklogItem, WorklogItem>?, TableCell<WorklogItem, WorklogItem>?> { WorklogActionsCell() }
    }
}