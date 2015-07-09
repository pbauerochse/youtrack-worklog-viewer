package de.pbauerochse.worklogviewer.fx.tablecolumns;

import de.pbauerochse.worklogviewer.WorklogViewer;
import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayRow;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.*;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class TaskDescriptionTreeTableColumn extends TreeTableColumn<DisplayRow, DisplayRow> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDescriptionTreeTableColumn.class);

    public TaskDescriptionTreeTableColumn() {
        super(FormattingUtil.getFormatted("view.main.issue"));
        setSortable(false);
        setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue()));
        setCellFactory(param -> {
            TreeTableCell<DisplayRow, DisplayRow> tableCell = new TreeTableCell<DisplayRow, DisplayRow>() {

                @Override
                protected void updateItem(DisplayRow item, boolean empty) {

                    getStyleClass().removeAll(ALL);

                    if (empty) {
                        setText(StringUtils.EMPTY);
                        setTooltip(null);
                    } else {
                        if (item.isGrandTotalSummary()) {
                            setText(FormattingUtil.getFormatted("view.main.summary"));
                            setTooltip(null);
                            getStyleClass().add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                            setAlignment(Pos.CENTER_RIGHT);
                        } else if (item.isGroupContainer()) {
                            setText(item.getLabel());
                            setTooltip(new Tooltip(item.getLabel()));
                            getStyleClass().add(GROUP_COLUMN_OR_CELL_CSS_CLASS);
                            setAlignment(Pos.CENTER_LEFT);
                        } else {
                            setText(item.getIssueId().get() + " - " + item.getLabel());
                            setTooltip(new Tooltip(getText()));
                            getStyleClass().add(ISSUE_CELL_CSS_CLASS);
                            setAlignment(Pos.CENTER_LEFT);
                        }
                    }
                }
            };

            tableCell.setOnMouseClicked(event -> {
                TreeTableCell<DisplayRow, DisplayRow> cell = (TreeTableCell<DisplayRow, DisplayRow>) event.getSource();

                DisplayRow clickedWorklogItem = cell.getTreeTableRow().getItem();
                if (clickedWorklogItem != null && !clickedWorklogItem.isGrandTotalSummary() && !clickedWorklogItem.isGroupContainer()) {
                    LOGGER.debug("Selected item {}", clickedWorklogItem.getLabel());
                    SettingsUtil.Settings settings = SettingsUtil.loadSettings();
                    String issueUrl = String.format("%s/issue/%s#tab=Time%%20Tracking", StringUtils.stripEnd(settings.getYoutrackUrl(), "/"), clickedWorklogItem.getIssueId().get());
                    Platform.runLater(() -> WorklogViewer.getInstance().getHostServices().showDocument(issueUrl));
                }
            });

            return tableCell;
        });
    }

}
