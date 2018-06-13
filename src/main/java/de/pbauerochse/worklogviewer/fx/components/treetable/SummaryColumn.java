package de.pbauerochse.worklogviewer.fx.components.treetable;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.scene.control.TreeTableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays the total amount of time spent
 * for each Issue, as well as a total time
 * spent row
 */
class SummaryColumn extends TreeTableColumn<TreeTableRowModel, TreeTableRowModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SummaryColumn.class);

    public SummaryColumn() {
        super(FormattingUtil.getFormatted("view.main.summary"));
        setSortable(false);
//        setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getValue()));
//        setCellFactory(param -> {
//            TreeTableCell<DisplayRow, DisplayRow> summaryCell = new TreeTableCell<DisplayRow, DisplayRow>() {
//
//                @Override
//                protected void updateItem(DisplayRow item, boolean empty) {
//                    super.updateItem(item, empty);
//
//                    getStyleClass().removeAll(ALL_WORKLOGVIEWER_CLASSES);
//
//                    if (empty) {
//                        setText(StringUtils.EMPTY);
//                    } else {
//                        setText(FormattingUtil.formatMinutes(item.getTotaltimeSpent()));
//                        if (item.isGroupContainer()) {
//                            getStyleClass().add(GROUP_COLUMN_OR_CELL_CSS_CLASS);
//                        } else {
//                            getStyleClass().add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
//                        }
//                    }
//                }
//            };
//
//            summaryCell.setAlignment(Pos.CENTER_RIGHT);
//            return summaryCell;
//        });

        setPrefWidth(120);
    }
}
