package de.pbauerochse.youtrack.fx.tablecolumns;

import de.pbauerochse.youtrack.WorklogViewer;
import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.pbauerochse.youtrack.fx.tablecolumns.CellStyleClasses.*;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class TaskDescriptionTreeTableColumn extends TreeTableColumn<TaskWithWorklogs, TaskWithWorklogs> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDescriptionTreeTableColumn.class);

    public TaskDescriptionTreeTableColumn() {
        super(FormattingUtil.getFormatted("view.main.issue"));
        setSortable(false);
        setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue()));
        setCellFactory(param -> {
            TreeTableCell<TaskWithWorklogs, TaskWithWorklogs> tableCell = new TreeTableCell<TaskWithWorklogs, TaskWithWorklogs>() {

                @Override
                protected void updateItem(TaskWithWorklogs item, boolean empty) {

                    getStyleClass().removeAll(ALL);

                    if (empty) {
                        setText(StringUtils.EMPTY);
                        setTooltip(null);
                    } else {
                        if (item.isSummaryRow()) {
                            setText(FormattingUtil.getFormatted("view.main.summary"));
                            setTooltip(null);
                            getStyleClass().add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                            setAlignment(Pos.CENTER_RIGHT);
                        } else if (item.isGroupRow()) {
                            setText(item.getIssue());
                            setTooltip(new Tooltip(item.getIssue()));
                            getStyleClass().add(GROUP_COLUMN_OR_CELL_CSS_CLASS);
                            setAlignment(Pos.CENTER_LEFT);
                        } else {
                            setText(item.getIssue() + " - " + item.getSummary());
                            setTooltip(new Tooltip(getText()));
                            getStyleClass().add(ISSUE_CELL_CSS_CLASS);
                            setAlignment(Pos.CENTER_LEFT);
                        }
                    }
                }
            };

            tableCell.setOnMouseClicked(event -> {
                TreeTableCell<TaskWithWorklogs, TaskWithWorklogs> cell = (TreeTableCell<TaskWithWorklogs, TaskWithWorklogs>) event.getSource();

                TaskWithWorklogs clickedWorklogItem = cell.getTreeTableRow().getItem();
                if (clickedWorklogItem != null && !clickedWorklogItem.isSummaryRow() && !clickedWorklogItem.isGroupRow()) {
                    LOGGER.debug("Selected item {}", clickedWorklogItem.getIssue());
                    SettingsUtil.Settings settings = SettingsUtil.loadSettings();
                    String issueUrl = String.format("%s/issue/%s#tab=Time%%20Tracking", StringUtils.stripEnd(settings.getYoutrackUrl(), "/"), clickedWorklogItem.getIssue());
                    Platform.runLater(() -> WorklogViewer.getInstance().getHostServices().showDocument(issueUrl));
                }
            });

            return tableCell;
        });
    }

}
