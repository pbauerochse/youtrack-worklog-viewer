package de.pbauerochse.worklogviewer.fx.converter;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class GroupByCategoryStringConverter extends StringConverter<GroupByCategory> {

    private ComboBox<GroupByCategory> categoryComboBox;

    public GroupByCategoryStringConverter(ComboBox<GroupByCategory> categoryComboBox) {
        this.categoryComboBox = categoryComboBox;
    }

    @Override
    public String toString(GroupByCategory category) {
        return Optional.ofNullable(category)
                .map(GroupByCategory::getName)
                .orElseGet(() -> FormattingUtil.getFormatted("view.main.groupby.nogroupby"));
    }

    @Override
    public GroupByCategory fromString(String categoryName) {
        // special "nothing-selected" item
        if (StringUtils.equals(FormattingUtil.getFormatted("view.main.groupby.nogroupby"), categoryName)) {
            return null;
        }

        return categoryComboBox.getItems().stream()
                .filter(category -> StringUtils.equals(category.getName(), categoryName))
                .findFirst()
                .orElse(null);
    }
}
