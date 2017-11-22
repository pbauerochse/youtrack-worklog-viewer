package de.pbauerochse.worklogviewer.excel;

import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayRow;
import javafx.scene.control.TreeItem;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public abstract class ExcelColumnRenderer {

    private static final int ADDITIONAL_ROW_HEIGHT = 5;

    public abstract void renderCells(int columnIndex, Sheet sheet, List<TreeItem<DisplayRow>> displayResult, boolean isGroupedData);

    private Font boldFont;
    private Font biggerBoldFont;
    private CellStyle boldCellStyle;
    private CellStyle boldRightAlignedCellStyle;
    private CellStyle regularRightAlignedCellStyle;
    private CellStyle groupHeadlineCellStyle;
    private CellStyle groupHeadlineWorklogCellStyle;

    protected void adjustRowHeight(Cell cellWithStyles) {
        Font font = cellWithStyles.getSheet().getWorkbook().getFontAt(cellWithStyles.getCellStyle().getFontIndex());
        short fontHeightInPoints = font.getFontHeightInPoints();
        Row row = cellWithStyles.getRow();
        row.setHeightInPoints(Math.max(fontHeightInPoints + ADDITIONAL_ROW_HEIGHT, row.getHeightInPoints()));
    }

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
            boldCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        }
        return boldCellStyle;
    }

    protected CellStyle getWorklogCellStyle(Sheet sheet) {
        if (regularRightAlignedCellStyle == null) {
            regularRightAlignedCellStyle = sheet.getWorkbook().createCellStyle();
            regularRightAlignedCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            regularRightAlignedCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        }
        return regularRightAlignedCellStyle;
    }

    protected CellStyle getWorklogSummaryCellStyle(Sheet sheet) {
        if (boldRightAlignedCellStyle == null) {
            boldRightAlignedCellStyle = sheet.getWorkbook().createCellStyle();
            boldRightAlignedCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            boldRightAlignedCellStyle.setFont(getBoldFont(sheet));
            boldRightAlignedCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        }
        return boldRightAlignedCellStyle;
    }

    protected CellStyle getGroupHeadlineCellStyle(Sheet sheet) {
        if (groupHeadlineCellStyle == null) {
            groupHeadlineCellStyle = sheet.getWorkbook().createCellStyle();
            groupHeadlineCellStyle.setFont(getBiggerBoldFont(sheet));
            groupHeadlineCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        }
        return groupHeadlineCellStyle;
    }

    protected CellStyle getGroupHeadlineWorklogCellStyle(Sheet sheet) {
        if (groupHeadlineWorklogCellStyle == null) {
            groupHeadlineWorklogCellStyle = sheet.getWorkbook().createCellStyle();
            groupHeadlineWorklogCellStyle.setFont(getBiggerBoldFont(sheet));
            groupHeadlineWorklogCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            groupHeadlineWorklogCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        }
        return groupHeadlineWorklogCellStyle;
    }

    private Font getBoldFont(Sheet sheet) {
        if (boldFont == null) {
            boldFont = sheet.getWorkbook().createFont();
            boldFont.setFontHeightInPoints((short) 12);
            boldFont.setBold(true);
        }
        return boldFont;
    }

    private Font getBiggerBoldFont(Sheet sheet) {
        if (biggerBoldFont == null) {
            biggerBoldFont = sheet.getWorkbook().createFont();
            biggerBoldFont.setBold(true);
            biggerBoldFont.setFontHeightInPoints((short) 14);
        }
        return biggerBoldFont;
    }
}
