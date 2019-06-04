package de.pbauerochse.worklogviewer.fx.issuesearch.details

import de.pbauerochse.worklogviewer.report.WorklogItem
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.WorklogTimeFormatter
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.util.Callback
import java.time.LocalDate

/**
 * Displays the formatted date of a WorklogItem
 */
class WorklogDateCell : TableCell<WorklogItem, LocalDate?>() {

    override fun updateItem(item: LocalDate?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.let { FormattingUtil.formatDate(it) }
    }

    companion object {
        val CELL_FACTORY = Callback<TableColumn<WorklogItem, LocalDate?>?, TableCell<WorklogItem, LocalDate?>?> { WorklogDateCell() }
    }
}

/**
 * Displays the formatted duration of the WorklogItem
 */
class WorklogDurationCell : TableCell<WorklogItem, Long?>() {
    override fun updateItem(item: Long?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.let {
            WorklogTimeFormatter(SettingsUtil.settingsViewModel.workhoursProperty.value).getFormatted(it)
        }
    }

    companion object {
        val CELL_FACTORY = Callback<TableColumn<WorklogItem, LocalDate?>?, TableCell<WorklogItem, LocalDate?>?> { WorklogDateCell() }
    }
}
