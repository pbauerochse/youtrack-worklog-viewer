package de.pbauerochse.worklogviewer.fx.converter;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.view.grouping.Grouping;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GroupingComboBoxConverter extends StringConverter<Grouping> {

    private final ComboBox<Grouping> categoryComboBox;

    public GroupingComboBoxConverter(@NotNull ComboBox<Grouping> categoryComboBox) {
        this.categoryComboBox = categoryComboBox;
    }

    @Override
    public String toString(Grouping category) {
        return Optional.ofNullable(category)
                .map(Grouping::getLabel)
                .orElseGet(() -> FormattingUtil.getFormatted("grouping.none"));
    }

    @Override
    public Grouping fromString(String categoryName) {
        // special "nothing-selected" item
        if (StringUtils.equals(FormattingUtil.getFormatted("grouping.none"), categoryName)) {
            return null;
        }

        return categoryComboBox.getItems().stream()
                .filter(category -> StringUtils.equals(category.getLabel(), categoryName))
                .findFirst()
                .orElse(null);
    }
}
