package de.pbauerochse.youtrack.excel.columns;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.excel.ExcelColumnRenderer;
import de.pbauerochse.youtrack.util.FormattingUtil;
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
    public void renderCells(int columnIndex, Sheet sheet, WorklogResult result, List<TreeItem<TaskWithWorklogs>> displayResult) {

        AtomicInteger currentRowIndex = new AtomicInteger(0);

        for (TreeItem<TaskWithWorklogs> taskWithWorklogsTreeItem : displayResult) {
            renderTreeItem(taskWithWorklogsTreeItem, sheet, currentRowIndex, columnIndex);
        }
    }

    private void renderTreeItem(TreeItem<TaskWithWorklogs> item, Sheet sheet, AtomicInteger rowIndex, int columnIndex) {
        TaskWithWorklogs taskWithWorklogs = item.getValue();

        Row row = getOrCreateRow(rowIndex.getAndIncrement(), sheet);

        Cell cell = row.createCell(columnIndex);

        if (taskWithWorklogs.isGroupRow()) {
            cell.setCellStyle(getGroupHeadlineCellStyle(sheet));
            adjustRowHeight(cell);
            rowIndex.getAndIncrement(); // spacing

            // headline and a bit of spacing
            row = getOrCreateRow(rowIndex.getAndAdd(2), sheet);
            cell = row.createCell(columnIndex);
            cell.setCellStyle(getHeadlineCellStyle(sheet));
            adjustRowHeight(cell);
            cell.setCellValue(FormattingUtil.getFormatted("view.main.summary"));

            item.getChildren().forEach(Child -> renderTreeItem(Child, sheet, rowIndex, columnIndex));

            // summary at the end
            row = getOrCreateRow(rowIndex.getAndIncrement(), sheet);
            cell = row.createCell(columnIndex);
            cell.setCellStyle(getWorklogSummaryCellStyle(sheet));
            adjustRowHeight(cell);
            cell.setCellValue(FormattingUtil.formatMinutes(taskWithWorklogs.getTotalInMinutes()));

            // additional spacing
            rowIndex.getAndAdd(4);
        } else if (taskWithWorklogs.isSummaryRow()) {
            cell.setCellStyle(getGroupHeadlineWorklogCellStyle(sheet));
            adjustRowHeight(cell);
            cell.setCellValue(FormattingUtil.formatMinutes(taskWithWorklogs.getTotalInMinutes()));
        } else {
            cell.setCellStyle(getWorklogSummaryCellStyle(sheet));
            adjustRowHeight(cell);
            cell.setCellValue(FormattingUtil.formatMinutes(taskWithWorklogs.getTotalInMinutes()));
        }
    }
}
