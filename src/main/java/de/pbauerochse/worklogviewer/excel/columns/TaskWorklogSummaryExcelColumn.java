package de.pbauerochse.worklogviewer.excel.columns;

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer;
import de.pbauerochse.worklogviewer.fx.components.domain.DisplayRow;
import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.scene.control.TreeItem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class TaskWorklogSummaryExcelColumn extends ExcelColumnRenderer {

    @Override
    public void renderCells(int columnIndex, Sheet sheet, List<TreeItem<DisplayRow>> displayResult, boolean isGrouped) {

        AtomicInteger currentRowIndex = new AtomicInteger(0);

        if (!isGrouped) {
            // no group by criteria -> render table headline now
            renderHeadline(currentRowIndex, sheet, columnIndex);
        }

        Settings settings = SettingsUtil.getSettings();
        for (TreeItem<DisplayRow> taskWithWorklogsTreeItem : displayResult) {
            renderTreeItem(taskWithWorklogsTreeItem, sheet, currentRowIndex, columnIndex, settings);
        }
    }

    private void renderTreeItem(TreeItem<DisplayRow> item, Sheet sheet, AtomicInteger rowIndex, int columnIndex, Settings settings) {
        DisplayRow displayRow = item.getValue();

        Row row = getOrCreateRow(rowIndex.getAndIncrement(), sheet);

        Cell cell = row.createCell(columnIndex);

        if (displayRow.isGroupContainer()) {
            cell.setCellStyle(getGroupHeadlineCellStyle(sheet));
            adjustRowHeight(cell);
            rowIndex.getAndIncrement(); // spacing

            // headline
            renderHeadline(rowIndex, sheet, columnIndex);

            item.getChildren().forEach(Child -> renderTreeItem(Child, sheet, rowIndex, columnIndex, settings));

            // summary at the end
            row = getOrCreateRow(rowIndex.getAndIncrement(), sheet);
            cell = row.createCell(columnIndex);
            cell.setCellStyle(getWorklogSummaryCellStyle(sheet));

            // additional spacing
            rowIndex.getAndAdd(4);
        } else if (displayRow.isGrandTotalSummary()) {
            cell.setCellStyle(getGroupHeadlineWorklogCellStyle(sheet));
        } else {
            cell.setCellStyle(getWorklogSummaryCellStyle(sheet));
        }

        if (settings.isShowDecimalHourTimesInExcelReport()) {
            cell.setCellValue(displayRow.getTotaltimeSpent() / 60d);
        } else {
            cell.setCellValue(FormattingUtil.formatMinutes(displayRow.getTotaltimeSpent()));
        }

        adjustRowHeight(cell);
    }

    private void renderHeadline(AtomicInteger rowIndex, Sheet sheet, int columnIndex) {
        Row row = getOrCreateRow(rowIndex.getAndAdd(2), sheet);
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(getHeadlineCellStyle(sheet));
        adjustRowHeight(cell);
        cell.setCellValue(FormattingUtil.getFormatted("view.main.summary"));
    }
}
