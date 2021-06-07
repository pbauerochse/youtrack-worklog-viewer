package de.pbauerochse.worklogviewer.fx.issuesearch.details

import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.timereport.WorkItem
import de.pbauerochse.worklogviewer.timereport.WorkItemType
import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.util.WorklogTimeFormatter
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Displays the formatted date of a WorklogItem
 */
class WorklogDateColumn : TableColumn<WorkItem, ZonedDateTime>(getFormatted("dialog.issuesearch.workitems.columns.date")) {
    init {
        cellValueFactory = PropertyValueFactory("workDate")
        cellFactory = WorklogDateCell.CELL_FACTORY
        isSortable = true
    }
}
private class WorklogDateCell : TableCell<WorkItem, ZonedDateTime>() {
    override fun updateItem(item: ZonedDateTime?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.let { FormattingUtil.formatLongDate(it.withZoneSameInstant(ZoneId.systemDefault()).toLocalDate()) }
    }

    companion object {
        val CELL_FACTORY = Callback<TableColumn<WorkItem, ZonedDateTime>?, TableCell<WorkItem, ZonedDateTime>?> { WorklogDateCell() }
    }
}

/**
 * Displays the type of the worklog item
 */
class WorklogTypeColumn : TableColumn<WorkItem, WorkItemType>(getFormatted("dialog.issuesearch.workitems.columns.type")) {
    init {
        cellValueFactory = PropertyValueFactory("workType")
        cellFactory = WorklogTypeCell.CELL_FACTORY
        isSortable = true
    }
}
private class WorklogTypeCell : TableCell<WorkItem, WorkItemType>() {
    override fun updateItem(item: WorkItemType?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.label
    }

    companion object {
        val CELL_FACTORY = Callback<TableColumn<WorkItem, WorkItemType>?, TableCell<WorkItem, WorkItemType>?> { WorklogTypeCell() }
    }
}

/**
 * Displays the author of the WorklogItem
 */
class WorklogAuthorColumn : TableColumn<WorkItem, String?>(getFormatted("dialog.issuesearch.workitems.columns.author")) {
    init {
        cellValueFactory = Callback<CellDataFeatures<WorkItem, String?>?, ObservableValue<String?>?> { SimpleStringProperty(it?.value?.owner?.label) }
        isSortable = true
    }
}

/**
 * Displays the description of the WorklogItem
 */
class WorklogDescriptionColumn : TableColumn<WorkItem, String?>(getFormatted("dialog.issuesearch.workitems.columns.description")) {
    init {
        cellValueFactory = PropertyValueFactory<WorkItem, String?>("description")
        isSortable = true
    }
}

/**
 * Displays the formatted duration of the WorklogItem
 */
class WorklogDurationColumn : TableColumn<WorkItem, Long>(getFormatted("dialog.issuesearch.workitems.columns.duration")) {
    init {
        cellValueFactory = PropertyValueFactory("durationInMinutes")
        cellFactory = WorklogDurationCell.CELL_FACTORY
        isSortable = true
    }
}
private class WorklogDurationCell : TableCell<WorkItem, Long>() {
    override fun updateItem(item: Long?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.let {
            WorklogTimeFormatter(SettingsUtil.settingsViewModel.workhoursProperty.value).getFormatted(it)
        }
    }

    companion object {
        val CELL_FACTORY = Callback<TableColumn<WorkItem, Long>?, TableCell<WorkItem, Long>?> { WorklogDurationCell() }
    }
}

class WorklogActionsColumn : TableColumn<WorkItem, WorkItem>() {
    init {
        cellFactory = WorklogActionsCell.CELL_FACTORY
        isSortable = false
    }
}
private class WorklogActionsCell : TableCell<WorkItem, WorkItem>() {
    override fun updateItem(item: WorkItem?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.let { "TODO" }
    }

    companion object {
        val CELL_FACTORY = Callback<TableColumn<WorkItem, WorkItem>?, TableCell<WorkItem, WorkItem>?> { WorklogActionsCell() }
    }
}