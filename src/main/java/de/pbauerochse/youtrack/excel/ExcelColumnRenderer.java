package de.pbauerochse.youtrack.excel;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.WorklogResult;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public abstract class ExcelColumnRenderer {

    public abstract void renderCells(int columnIndex, Sheet sheet, WorklogResult result, List<TaskWithWorklogs> displayResult);

    private Font boldFont;
    private CellStyle boldCellStyle;
    private CellStyle boldRightAlignedCellStyle;
    private CellStyle regularRightAlignedCellStyle;

    protected Row getOrCreateRow(int rowIndex, Sheet sheet) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }

    protected CellStyle getHeadlineCellStyle(Sheet sheet) {
        if (boldCellStyle == null) {
            boldCellStyle = sheet.getWorkbook().createCellStyle();
            boldCellStyle.setFont(getBoldFont(sheet));
            boldCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        }
        return boldCellStyle;
    }

    protected CellStyle getWorklogCellStyle(Sheet sheet) {
        if (regularRightAlignedCellStyle == null) {
            regularRightAlignedCellStyle = sheet.getWorkbook().createCellStyle();
            regularRightAlignedCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        }
        return regularRightAlignedCellStyle;
    }

    protected CellStyle getWorklogSummaryCellStyle(Sheet sheet) {
        if (boldRightAlignedCellStyle == null) {
            boldRightAlignedCellStyle = sheet.getWorkbook().createCellStyle();
            boldRightAlignedCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            boldRightAlignedCellStyle.setFont(getBoldFont(sheet));
        }
        return boldRightAlignedCellStyle;
    }

    private Font getBoldFont(Sheet sheet) {
        if (boldFont == null) {
            boldFont = sheet.getWorkbook().createFont();
            boldFont.setBold(true);
        }
        return boldFont;
    }
}
