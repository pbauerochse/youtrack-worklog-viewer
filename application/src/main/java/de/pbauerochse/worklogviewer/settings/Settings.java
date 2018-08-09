package de.pbauerochse.worklogviewer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.fx.Theme;
import de.pbauerochse.worklogviewer.settings.jackson.LocalDateDeserializer;
import de.pbauerochse.worklogviewer.settings.jackson.LocalDateSerializer;
import de.pbauerochse.worklogviewer.settings.jackson.WeekdaySettingsDeserializer;
import de.pbauerochse.worklogviewer.settings.jackson.WeekdaySettingsSerializer;

import java.time.LocalDate;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

/**
 * Contains all settings for the YouTrack Worklog Viewer
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {

    private WindowSettings windowSettings = new WindowSettings();
    private YouTrackConnectionSettings youTrackConnectionSettings = new YouTrackConnectionSettings();

    private Theme theme = Theme.DARK;
    private int workHoursADay = 8;

    private ReportTimerange lastUsedReportTimerange = ReportTimerange.THIS_WEEK;
    private LocalDate startDate;
    private LocalDate endDate;

    private String lastUsedGroupByCategoryId;
    private boolean loadDataAtStartup = false;
    private boolean showStatistics = true;
    private boolean showAllWorklogs = true;
    private boolean showDecimalHourTimesInExcelReport = false;
    private WeekdaySettings collapseState = new WeekdaySettings(SATURDAY, SUNDAY);
    private WeekdaySettings highlightState = new WeekdaySettings(SATURDAY, SUNDAY);

    /**
     * Settings regarding the main application window
     */
    @JsonProperty("window")
    public WindowSettings getWindowSettings() {
        return windowSettings;
    }

    public void setWindowSettings(WindowSettings windowSettings) {
        this.windowSettings = windowSettings;
    }

    /**
     * Any settings required to access the YouTrack API
     */
    @JsonProperty("youtrack")
    public YouTrackConnectionSettings getYouTrackConnectionSettings() {
        return youTrackConnectionSettings;
    }

    public void setYouTrackConnectionSettings(YouTrackConnectionSettings youTrackConnectionSettings) {
        this.youTrackConnectionSettings = youTrackConnectionSettings;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * The amount of hours the user has to work on
     * any given day. Determines, how the YouTrack
     * times are being displayed:
     * <p>
     * - 9h booked hours on a 8h workday -> 1d 1h
     * - 9h booked hours on a 6h workday -> 1d 3h
     * - 9h booked hours on a 9h workday -> 1d
     */
    public int getWorkHoursADay() {
        return workHoursADay;
    }

    public void setWorkHoursADay(int workHoursADay) {
        this.workHoursADay = workHoursADay;
    }

    public ReportTimerange getLastUsedReportTimerange() {
        return lastUsedReportTimerange;
    }

    public void setLastUsedReportTimerange(ReportTimerange lastUsedReportTimerange) {
        this.lastUsedReportTimerange = lastUsedReportTimerange;
    }

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getLastUsedGroupByCategoryId() {
        return lastUsedGroupByCategoryId;
    }

    public void setLastUsedGroupByCategoryId(String lastUsedGroupByCategoryId) {
        this.lastUsedGroupByCategoryId = lastUsedGroupByCategoryId;
    }

    public boolean isLoadDataAtStartup() {
        return loadDataAtStartup;
    }

    public void setLoadDataAtStartup(boolean loadDataAtStartup) {
        this.loadDataAtStartup = loadDataAtStartup;
    }

    public boolean isShowStatistics() {
        return showStatistics;
    }

    public void setShowStatistics(boolean showStatistics) {
        this.showStatistics = showStatistics;
    }

    public boolean isShowAllWorklogs() {
        return showAllWorklogs;
    }

    public void setShowAllWorklogs(boolean showAllWorklogs) {
        this.showAllWorklogs = showAllWorklogs;
    }

    public boolean isShowDecimalHourTimesInExcelReport() {
        return showDecimalHourTimesInExcelReport;
    }

    public void setShowDecimalHourTimesInExcelReport(boolean showDecimalHourTimesInExcelReport) {
        this.showDecimalHourTimesInExcelReport = showDecimalHourTimesInExcelReport;
    }

    @JsonSerialize(using = WeekdaySettingsSerializer.class)
    @JsonDeserialize(using = WeekdaySettingsDeserializer.class)
    public WeekdaySettings getCollapseState() {
        return collapseState;
    }

    public void setCollapseState(WeekdaySettings collapseState) {
        this.collapseState = collapseState;
    }

    @JsonSerialize(using = WeekdaySettingsSerializer.class)
    @JsonDeserialize(using = WeekdaySettingsDeserializer.class)
    public WeekdaySettings getHighlightState() {
        return highlightState;
    }

    public void setHighlightState(WeekdaySettings highlightState) {
        this.highlightState = highlightState;
    }

}
