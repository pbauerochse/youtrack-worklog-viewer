package de.pbauerochse.youtrack.excel.columns;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.excel.ExcelColumnRenderer;
import de.pbauerochse.youtrack.util.FormattingUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class WorkdayColumn extends ExcelColumnRenderer {

    private final String caption;
    private final LocalDate localDate;

    public WorkdayColumn(String caption, LocalDate localDate) {
        this.caption = caption;
        this.localDate = localDate;
    }

    @Override
    public void renderCells(int columnIndex, Sheet sheet, WorklogResult result, List<TaskWithWorklogs> displayResult) {

        Row row = getOrCreateRow(0, sheet);

        // headline
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(getHeadlineCellStyle(sheet));
        cell.setCellValue(caption);

        for (int i = 0; i < displayResult.size(); i++) {
            TaskWithWorklogs taskWithWorklogs = displayResult.get(i);

            row = getOrCreateRow(i + 1, sheet);
            cell = row.createCell(columnIndex);

            if (taskWithWorklogs.isSummaryRow()) {
                cell.setCellStyle(getWorklogSummaryCellStyle(sheet));
            } else {
                cell.setCellStyle(getWorklogCellStyle(sheet));
            }

            cell.setCellValue(FormattingUtil.formatMinutes(taskWithWorklogs.getTotalInMinutes(localDate)));
        }
    }
}
