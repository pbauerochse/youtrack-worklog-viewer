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
public class GroupByCategoryStringConverter extends StringConverter<Optional<GroupByCategory>> {

    private ComboBox<Optional<GroupByCategory>> categoryComboBox;

    public GroupByCategoryStringConverter(ComboBox<Optional<GroupByCategory>> categoryComboBox) {
        this.categoryComboBox = categoryComboBox;
    }

    @Override
    public String toString(Optional<GroupByCategory> object) {
        if (!object.isPresent()) {
            return FormattingUtil.getFormatted("view.main.groupby.nogroupby");
        }

        return object.get().getName();
    }

    @Override
    public Optional<GroupByCategory> fromString(String string) {
        for (Optional<GroupByCategory> groupByCategory : categoryComboBox.getItems()) {
            if (groupByCategory.isPresent() && StringUtils.equals(groupByCategory.get().getName(), string)) {
                return groupByCategory;
            }
        }

        return Optional.empty();
    }
}
