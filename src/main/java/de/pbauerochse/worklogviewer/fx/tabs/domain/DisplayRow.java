package de.pbauerochse.worklogviewer.fx.tabs.domain;

import com.google.common.collect.Maps;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Patrick Bauerochse
 * @since 09.07.15
 */
public class DisplayRow {

    public String label;

    public Optional<String> issueId = Optional.empty();

    private Map<LocalDate, DisplayDayEntry> timespanEntries = Maps.newHashMap();

    private boolean isGrandTotalSummary;

    private boolean isGroupContainer;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Optional<String> getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = Optional.of(issueId);
    }

    public long getTotaltimeSpent() {
        AtomicLong sum = new AtomicLong(0);
        timespanEntries.values().forEach(entry -> sum.addAndGet(entry.getSpentTime().get()));
        return sum.longValue();
    }

    public boolean isGrandTotalSummary() {
        return isGrandTotalSummary;
    }

    public void setIsGrandTotalSummary(boolean isGrandTotalSummary) {
        this.isGrandTotalSummary = isGrandTotalSummary;
    }

    public boolean isGroupContainer() {
        return isGroupContainer;
    }

    public void setIsGroupContainer(boolean isGroupContainer) {
        this.isGroupContainer = isGroupContainer;
    }

    public Optional<DisplayDayEntry> getWorkdayEntry(LocalDate date) {
        return Optional.ofNullable(timespanEntries.get(date));
    }

    public void addDisplayDayEntry(DisplayDayEntry entry) {
        if (timespanEntries.containsKey(entry.getDate())) throw new IllegalStateException();
        timespanEntries.put(entry.getDate(), entry);
    }
}
