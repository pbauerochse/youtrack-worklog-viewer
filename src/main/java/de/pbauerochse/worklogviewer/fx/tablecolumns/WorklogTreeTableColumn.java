package de.pbauerochse.worklogviewer.fx.tablecolumns;

import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayRow;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.*;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class WorklogTreeTableColumn extends TreeTableColumn<DisplayRow, DisplayRow> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorklogTreeTableColumn.class);

    public WorklogTreeTableColumn(String displayDate, LocalDate currentColumnDate) {
        super(displayDate);
        setSortable(false);
        setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue()));
        setCellFactory(param -> {
            TreeTableCell<DisplayRow, DisplayRow> cell = new TreeTableCell<DisplayRow, DisplayRow>() {
                @Override
                protected void updateItem(DisplayRow item, boolean empty) {
                    super.updateItem(item, empty);

                    getStyleClass().removeAll(ALL);
                    setText(StringUtils.EMPTY);
                    setTooltip(null);

                    if (!empty) {
                        // display the spent time as cell value
                        // and the date with the spent time as tooltip
                        item.getWorkdayEntry(currentColumnDate)
                            .ifPresent(workdayEntry -> {
                                setText(FormattingUtil.formatMinutes(workdayEntry.getSpentTime().get()));
                                setTooltip(new Tooltip(displayDate + " - " + getText()));
                            });

                        if (isWeekend(currentColumnDate)) {
                            setPrefWidth(20);
                            getStyleClass().add(WEEKEND_COLUMN_OR_CELL_CSS_CLASS);
                        } else {
                            setPrefWidth(100);
                        }

                        if (isToday(currentColumnDate)) {
                            getStyleClass().add(TODAY_COLUMN_OR_CELL_CSS_CLASS);
                        }

                        if (item.isGroupContainer()) {
                            getStyleClass().add(GROUP_COLUMN_OR_CELL_CSS_CLASS);
                        } else if (item.isGrandTotalSummary()) {
                            getStyleClass().add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                        }
                    }
                }
            };

            cell.setAlignment(Pos.CENTER_RIGHT);
            return cell;
        });

        if (isWeekend(currentColumnDate)) {
            setPrefWidth(20);
            getStyleClass().add(WEEKEND_COLUMN_OR_CELL_CSS_CLASS);
        } else {
            setPrefWidth(100);
        }

        if (isToday(currentColumnDate)) {
            getStyleClass().add(TODAY_COLUMN_OR_CELL_CSS_CLASS);
        }
    }

    private static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private static boolean isToday(LocalDate date) {
        return date.isEqual(LocalDate.now());
    }
}
