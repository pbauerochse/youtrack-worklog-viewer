package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.excel.ExcelExporter;
import de.pbauerochse.worklogviewer.fx.components.treetable.WorklogsTreeTableViewData;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Task the triggers the Excel Report generation
 * of the given data
 */
public class ExportToExcelTask extends Task<File> {

    private final String text;
    private final WorklogsTreeTableViewData data;
    private final File targetFile;

    public ExportToExcelTask(String text, WorklogsTreeTableViewData data, File targetFile) {
        updateTitle(text + "-ExcelExport-Task");
        this.text = text;
        this.data = data;
        this.targetFile = targetFile;
    }

    @Override
    protected File call() throws Exception {
        updateMessage(FormattingUtil.getFormatted("worker.excel.exporting"));
        updateProgress(0, 1);

        Workbook workbook = ExcelExporter.createWorkbook(text, data);

        try (OutputStream outputStream = new FileOutputStream(targetFile)) {
            workbook.write(outputStream);
            updateProgress(1, 1);
        }

        return targetFile;
    }

}
