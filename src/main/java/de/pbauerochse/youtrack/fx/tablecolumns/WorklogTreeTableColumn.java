package de.pbauerochse.youtrack.fx.tablecolumns;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.util.FormattingUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static de.pbauerochse.youtrack.fx.tablecolumns.CellStyleClasses.*;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class WorklogTreeTableColumn extends TreeTableColumn<TaskWithWorklogs, TaskWithWorklogs> {

    public WorklogTreeTableColumn(String displayDate, LocalDate currentColumnDate) {
        super(displayDate);
        setSortable(false);
        setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue()));
        setCellFactory(param -> {
            TreeTableCell<TaskWithWorklogs, TaskWithWorklogs> cell = new TreeTableCell<TaskWithWorklogs, TaskWithWorklogs>() {
                @Override
                protected void updateItem(TaskWithWorklogs item, boolean empty) {
                    super.updateItem(item, empty);

                    getStyleClass().removeAll(ALL);

                    if (empty) {
                        // clear cell and tooltip
                        setText(StringUtils.EMPTY);
                        setTooltip(null);
                    } else {
                        // display the spent time as cell value
                        // and the date with the spent time as tooltip
                        String worklogTimeFormatted = FormattingUtil.formatMinutes(item.getTotalInMinutes(currentColumnDate));

                        setText(worklogTimeFormatted);
                        setTooltip(new Tooltip(displayDate + " - " + worklogTimeFormatted));

                        if (isWeekend(currentColumnDate)) {
                            setPrefWidth(20);
                            getStyleClass().add(WEEKEND_COLUMN_OR_CELL_CSS_CLASS);
                        } else {
                            setPrefWidth(100);
                        }

                        if (isToday(currentColumnDate)) {
                            getStyleClass().add(TODAY_COLUMN_OR_CELL_CSS_CLASS);
                        }

                        if (item.isGroupRow()) {
                            getStyleClass().add(GROUP_COLUMN_OR_CELL_CSS_CLASS);
                        } else if (item.isSummaryRow()) {
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
