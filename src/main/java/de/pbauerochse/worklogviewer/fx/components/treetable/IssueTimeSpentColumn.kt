package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.settings.Settings
import de.pbauerochse.worklogviewer.util.FormattingUtil.formatDate
import javafx.scene.control.TreeTableColumn
import java.time.LocalDate

/**
 * Displays the total spent time at a given
 * day for the Issue
 */
internal class IssueTimeSpentColumn : TreeTableColumn<TreeTableRowModel, TreeTableRowModel>() {

    init {
//
//        val settings = SettingsUtil.settings
//
//        isSortable = false
//        setCellValueFactory { param -> SimpleObjectProperty(param.value.value) }
//        setCellFactory { param ->
//            val cell = object : TreeTableCell<DisplayRow, DisplayRow>() {
//                override fun updateItem(item: DisplayRow, empty: Boolean) {
//                    super.updateItem(item, empty)
//
//                    styleClass.removeAll(ALL_WORKLOGVIEWER_CLASSES)
//                    text = StringUtils.EMPTY
//                    tooltip = null
//
//                    if (!empty) {
//                        // display the spent time as cell value
//                        // and the date with the spent time as tooltip
//                        item.getWorkdayEntry(currentColumnDate)
//                            .ifPresent { workdayEntry ->
//                                text = FormattingUtil.formatMinutes(workdayEntry.spentTime.get())
//                                tooltip = Tooltip(displayDate + " - " + text)
//                            }
//
//                        //                        if (isCollapsed(currentColumnDate, settings)) {
//                        //                            setPrefWidth(20);
//                        //                        } else {
//                        //                            setPrefWidth(100);
//                        //                        }
//
//                        if (isToday(currentColumnDate)) {
//                            styleClass.add(TODAY_COLUMN_OR_CELL_CSS_CLASS)
//                        } else if (isHighlighted(currentColumnDate, settings)) {
//                            styleClass.add(HIGHLIGHT_COLUMN_CSS_CLASS)
//                        }
//
//                        if (item.isGroupContainer) {
//                            styleClass.add(GROUP_COLUMN_OR_CELL_CSS_CLASS)
//                        } else if (item.isGrandTotalSummary) {
//                            styleClass.add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS)
//                        }
//                    }
//                }
//            }
//
//            cell.alignment = Pos.CENTER_RIGHT
//            cell
//        }
//
//        if (isCollapsed(currentColumnDate, settings)) {
//            prefWidth = 20.0
//        } else {
//            prefWidth = 100.0
//        }
//
//        if (isToday(currentColumnDate)) {
//            styleClass.add(TODAY_COLUMN_OR_CELL_CSS_CLASS)
//        } else if (isHighlighted(currentColumnDate, settings)) {
//            styleClass.add(HIGHLIGHT_COLUMN_CSS_CLASS)
//        }
    }

    fun update(date: LocalDate) {
        text = formatDate(date)
    }

    private fun isCollapsed(date: LocalDate, settings: Settings): Boolean {
        val dayOfWeek = date.dayOfWeek
        return settings.collapseState.isSet(dayOfWeek)
    }

    private fun isHighlighted(date: LocalDate, settings: Settings): Boolean {
        val dayOfWeek = date.dayOfWeek
        return settings.highlightState.isSet(dayOfWeek)
    }

    private fun isToday(date: LocalDate): Boolean {
        return date.isEqual(LocalDate.now())
    }


}
