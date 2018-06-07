package de.pbauerochse.worklogviewer.fx.tablecolumns;

import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayRow;
import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses.*;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class WorklogTreeTableColumn extends TreeTableColumn<DisplayRow, DisplayRow> {

    public WorklogTreeTableColumn(String displayDate, LocalDate currentColumnDate) {
        super(displayDate);
        Settings settings = SettingsUtil.getSettings();

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

//                        if (isCollapsed(currentColumnDate, settings)) {
//                            setPrefWidth(20);
//                        } else {
//                            setPrefWidth(100);
//                        }

                        if (isToday(currentColumnDate)) {
                            getStyleClass().add(TODAY_COLUMN_OR_CELL_CSS_CLASS);
                        } else if (isHighlighted(currentColumnDate, settings)) {
                            getStyleClass().add(HIGHLIGHT_COLUMN_CSS_CLASS);
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

        if (isCollapsed(currentColumnDate, settings)) {
            setPrefWidth(20);
        } else {
            setPrefWidth(100);
        }

        if (isToday(currentColumnDate)) {
            getStyleClass().add(TODAY_COLUMN_OR_CELL_CSS_CLASS);
        } else if (isHighlighted(currentColumnDate, settings)) {
            getStyleClass().add(HIGHLIGHT_COLUMN_CSS_CLASS);
        }
    }

    private static boolean isCollapsed(LocalDate date, Settings settings) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return settings.getCollapseState().isSet(dayOfWeek);
    }

    private static boolean isHighlighted(LocalDate date, Settings settings) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return settings.getHighlightState().isSet(dayOfWeek);
    }

    private static boolean isToday(LocalDate date) {
        return date.isEqual(LocalDate.now());
    }
}
