package de.pbauerochse.worklogviewer.fx.tablecolumns;

import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayRow;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.*;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class TaskWorklogSummaryTreeTableColumn extends TreeTableColumn<DisplayRow, DisplayRow> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskWorklogSummaryTreeTableColumn.class);

    public TaskWorklogSummaryTreeTableColumn() {
        super(FormattingUtil.getFormatted("view.main.summary"));
        setSortable(false);
        setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getValue()));
        setCellFactory(param -> {
            TreeTableCell<DisplayRow, DisplayRow> summaryCell = new TreeTableCell<DisplayRow, DisplayRow>() {

                @Override
                protected void updateItem(DisplayRow item, boolean empty) {
                    super.updateItem(item, empty);

                    getStyleClass().removeAll(ALL);

                    if (empty) {
                        setText(StringUtils.EMPTY);
                    } else {
                        setText(FormattingUtil.formatMinutes(item.getTotaltimeSpent()));
                        if (item.isGroupContainer()) {
                            getStyleClass().add(GROUP_COLUMN_OR_CELL_CSS_CLASS);
                        } else {
                            getStyleClass().add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                        }
                    }
                }
            };

            summaryCell.setAlignment(Pos.CENTER_RIGHT);
            return summaryCell;
        });

        setPrefWidth(120);
    }
}
