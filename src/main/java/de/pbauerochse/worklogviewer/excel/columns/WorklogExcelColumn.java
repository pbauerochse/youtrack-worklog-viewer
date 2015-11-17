package de.pbauerochse.worklogviewer.excel.columns;

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer;
import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayDayEntry;
import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayRow;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import javafx.scene.control.TreeItem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class WorklogExcelColumn extends ExcelColumnRenderer {

    private final String caption;
    private final LocalDate localDate;

    public WorklogExcelColumn(String caption, LocalDate localDate) {
        this.caption = caption;
        this.localDate = localDate;
    }

    @Override
    public void renderCells(int columnIndex, Sheet sheet, List<TreeItem<DisplayRow>> displayResult, boolean isGrouped) {

        AtomicInteger currentRowIndex = new AtomicInteger(0);

        if (!isGrouped) {
            renderHeadline(currentRowIndex, sheet, columnIndex);
        }

        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        for (TreeItem<DisplayRow> taskWithWorklogsTreeItem : displayResult) {
            renderTreeItem(taskWithWorklogsTreeItem, sheet, currentRowIndex, columnIndex, settings);
        }
    }

    private void renderTreeItem(TreeItem<DisplayRow> item, Sheet sheet, AtomicInteger rowIndex, int columnIndex, SettingsUtil.Settings settings) {
        DisplayRow displayRow = item.getValue();

        Row row = getOrCreateRow(rowIndex.getAndIncrement(), sheet);

        Cell cell = row.createCell(columnIndex);
        Optional<DisplayDayEntry> workdayEntryOptional = displayRow.getWorkdayEntry(localDate);

        if (displayRow.isGroupContainer()) {
            cell.setCellStyle(getGroupHeadlineCellStyle(sheet));
            rowIndex.getAndIncrement(); // spacing

            // headline
            renderHeadline(rowIndex, sheet, columnIndex);

            item.getChildren().forEach(Child -> renderTreeItem(Child, sheet, rowIndex, columnIndex, settings));

            // add summary at the end
            row = getOrCreateRow(rowIndex.getAndIncrement(), sheet);

            cell = row.createCell(columnIndex);
            cell.setCellStyle(getWorklogSummaryCellStyle(sheet));

            // additional spacing
            rowIndex.getAndAdd(4);
        } else if (displayRow.isGrandTotalSummary()) {
            cell.setCellStyle(getGroupHeadlineWorklogCellStyle(sheet));
        } else {
            cell.setCellStyle(getWorklogCellStyle(sheet));
        }

        if (workdayEntryOptional.isPresent()) {
            if (settings.isShowDecimalHourTimesInExcelReport()) {
                cell.setCellValue(workdayEntryOptional.get().getSpentTime().get() / 60d);
            } else {
                cell.setCellValue(FormattingUtil.formatMinutes(workdayEntryOptional.get().getSpentTime().get()));
            }
        }
    }

    private void renderHeadline(AtomicInteger rowIndex, Sheet sheet, int columnIndex) {
        Row row = getOrCreateRow(rowIndex.getAndAdd(2), sheet);
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(getHeadlineCellStyle(sheet));
        cell.setCellValue(caption);
    }
}
