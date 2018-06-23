package de.pbauerochse.worklogviewer.fx.converter;

import de.pbauerochse.worklogviewer.connector.GroupByParameter;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class GroupByCategoryStringConverter extends StringConverter<GroupByParameter> {

    private ComboBox<GroupByParameter> categoryComboBox;

    public GroupByCategoryStringConverter(ComboBox<GroupByParameter> categoryComboBox) {
        this.categoryComboBox = categoryComboBox;
    }

    @Override
    public String toString(GroupByParameter category) {
        return Optional.ofNullable(category)
                .map(GroupByParameter::getLabel)
                .orElseGet(() -> FormattingUtil.getFormatted("view.main.groupby.nogroupby"));
    }

    @Override
    public GroupByParameter fromString(String categoryName) {
        // special "nothing-selected" item
        if (StringUtils.equals(FormattingUtil.getFormatted("view.main.groupby.nogroupby"), categoryName)) {
            return null;
        }

        return categoryComboBox.getItems().stream()
                .filter(category -> StringUtils.equals(category.getLabel(), categoryName))
                .findFirst()
                .orElse(null);
    }
}
