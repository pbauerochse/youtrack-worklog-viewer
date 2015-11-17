package de.pbauerochse.worklogviewer.fx.tablecolumns;

import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayRow;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.ImageView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class TaskStatusTreeTableColumn extends TreeTableColumn<DisplayRow, Optional<LocalDateTime>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusTreeTableColumn.class);

    public TaskStatusTreeTableColumn() {
        super(FormattingUtil.getFormatted("view.main.resolved"));

        setSortable(false);
        setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getValue().getResolvedDate()));
        setCellFactory(param -> {
            TreeTableCell<DisplayRow, Optional<LocalDateTime>> statusCell = new TreeTableCell<DisplayRow, Optional<LocalDateTime>>() {

                @Override
                protected void updateItem(Optional<LocalDateTime> item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(StringUtils.EMPTY);

                    if (empty || !item.isPresent()) {
                        setGraphic(null);
                        setTooltip(null);
                    } else {
                        LOGGER.debug("Setting graphic on column with a resolved date");
                        setGraphic(new ImageView("/fx/img/accept.png"));
                        setTooltip(new Tooltip(FormattingUtil.formatDateTime(item.get())));
                    }
                }
            };

            statusCell.setAlignment(Pos.CENTER);
            return statusCell;
        });

        setPrefWidth(120);
    }
}
