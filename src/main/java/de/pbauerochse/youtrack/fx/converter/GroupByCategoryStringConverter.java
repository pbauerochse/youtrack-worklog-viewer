package de.pbauerochse.youtrack.fx.converter;

import de.pbauerochse.youtrack.domain.GroupByCategory;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

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
    public String toString(GroupByCategory object) {
        return object.getName();
    }

    @Override
    public GroupByCategory fromString(String string) {

        for (GroupByCategory groupByCategory : categoryComboBox.getItems()) {
            if (StringUtils.equals(groupByCategory.getName(), string)) {
                return groupByCategory;
            }
        }

        return null;
    }
}
