package de.pbauerochse.worklogviewer.excel.columns;

import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class TaskStatusExcelColumn extends ExcelColumnRenderer {

//    @Override
//    public void renderCells(int columnIndex, Sheet sheet, List<TreeItem<DisplayRow>> displayResult, boolean isGrouped) {
//
//        AtomicInteger currentRowIndex = new AtomicInteger(0);
//
//        if (!isGrouped) {
//            renderHeadline(currentRowIndex, sheet, columnIndex);
//        }
//
//        for (TreeItem<DisplayRow> taskWithWorklogsTreeItem : displayResult) {
//            renderTreeItem(taskWithWorklogsTreeItem, sheet, currentRowIndex, columnIndex);
//        }
//    }
//
//    private void renderTreeItem(TreeItem<DisplayRow> item, Sheet sheet, AtomicInteger rowIndex, int columnIndex) {
//        DisplayRow displayRow = item.getValue();
//
//        Row row = getOrCreateRow(rowIndex.getAndIncrement(), sheet);
//
//        Cell cell = row.createCell(columnIndex);
//
//        if (displayRow.isGroupContainer()) {
//            cell.setCellStyle(getGroupHeadlineCellStyle(sheet));
//            cell.setCellValue(displayRow.getLabel());
//            adjustRowHeight(cell);
//            rowIndex.getAndIncrement(); // spacing
//
//            // headline and a bit of spacing
//            renderHeadline(rowIndex, sheet, columnIndex);
//
//            item.getChildren().forEach(Child -> renderTreeItem(Child, sheet, rowIndex, columnIndex));
//
//            // add summary at the end
//            rowIndex.getAndAdd(5);
//        } else if (!displayRow.isGrandTotalSummary() && displayRow.getResolvedDate().isPresent()) {
//            cell.setCellValue(FormattingUtil.formatDateTime(displayRow.getResolvedDate().get()));
//        }
//    }

    private void renderHeadline(AtomicInteger rowIndex, Sheet sheet, int columnIndex) {
        Row row = getOrCreateRow(rowIndex.getAndAdd(2), sheet);
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(getHeadlineCellStyle(sheet));
        adjustRowHeight(cell);
        cell.setCellValue(FormattingUtil.getFormatted("view.main.resolved"));
    }
}
