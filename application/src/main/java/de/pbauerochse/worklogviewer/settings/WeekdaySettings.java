package de.pbauerochse.worklogviewer.settings;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WeekdaySettings {

    private final Set<DayOfWeek> enabledDayOfWeeks = new HashSet<>();

    public WeekdaySettings(DayOfWeek... initialState) {
        if (initialState != null) {
            enabledDayOfWeeks.addAll(Arrays.asList(initialState));
        }
    }

    public void set(@NotNull DayOfWeek day, boolean enabled) {
        if (enabled) {
            enabledDayOfWeeks.add(day);
        } else {
            enabledDayOfWeeks.remove(day);
        }
    }

    public void set(@NotNull Set<DayOfWeek> all) {
        enabledDayOfWeeks.clear();
        enabledDayOfWeeks.addAll(all);
    }

    public List<DayOfWeek> getAll() {
        return enabledDayOfWeeks.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public boolean isSet(@NotNull DayOfWeek dayOfWeek) {
        return enabledDayOfWeeks.contains(dayOfWeek);
    }
}
