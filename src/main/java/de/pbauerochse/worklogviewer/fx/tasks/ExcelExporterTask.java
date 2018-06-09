package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.fx.tabs.WorklogTab;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.concurrent.Task;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Patrick Bauerochse
 * @since 06.07.15
 */
public class ExcelExporterTask extends Task<File> {

    private final WorklogTab tab;
    private final File targetFile;

    public ExcelExporterTask(WorklogTab tab, File targetFile) {
        updateTitle(tab.getText() + "-ExcelExport-Task");
        this.tab = tab;
        this.targetFile = targetFile;
    }

    @Override
    protected File call() throws Exception {
        updateMessage(FormattingUtil.getFormatted("worker.excel.exporting"));
        updateProgress(0, 1);

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(tab.getText());
//        tab.writeDataToExcel(sheet);
        workbook.write(new FileOutputStream(targetFile));

        updateProgress(1, 1);

        return targetFile;
    }

}
