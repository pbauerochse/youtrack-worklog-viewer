package de.pbauerochse.worklogviewer.fx.converter;

import de.pbauerochse.worklogviewer.timerange.TimerangeProvider;
import de.pbauerochse.worklogviewer.timerange.TimerangeProviders;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimerangeProviderStringConverter extends StringConverter<TimerangeProvider> {

    @Override
    public String toString(@NotNull TimerangeProvider timerangeProvider) {
        return timerangeProvider.getLabel();
    }

    @Override
    @Nullable
    public TimerangeProvider fromString(String label) {
        return TimerangeProviders.getAllTimerangeProviders().stream()
                .filter(it -> it.getLabel().equals(label))
                .findFirst()
                .orElse(null);
    }

}
