import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.domain.timerangeprovider.TimerangeProviderFactory;
import de.pbauerochse.worklogviewer.fx.components.tabs.TimeReportResultTabbedPane;
import de.pbauerochse.worklogviewer.youtrack.TimeReport;
import de.pbauerochse.worklogviewer.youtrack.TimeReportParameters;
import de.pbauerochse.worklogviewer.youtrack.csv.CsvReportData;
import de.pbauerochse.worklogviewer.youtrack.csv.CsvReportReader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class Rakete extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        TimeReportResultTabbedPane tabbedPane = new TimeReportResultTabbedPane();

        Parent root = new BorderPane(tabbedPane);
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add("/fx/css/base-styling.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        TimerangeProvider timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(ReportTimerange.LAST_WEEK, null, null);
        CsvReportData csvReportData = CsvReportReader.processResponse(new FileInputStream("/home/patrick/IdeaProjects/youtrack-worklog-viewer/src/test/resources/report/example-response.csv"));
        TimeReport report = new TimeReport(new TimeReportParameters(timerangeProvider, null), csvReportData);
        tabbedPane.update(report);
    }
}
