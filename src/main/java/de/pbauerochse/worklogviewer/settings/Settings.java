package de.pbauerochse.worklogviewer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.settings.jackson.WeekdaySettingsDeserializer;
import de.pbauerochse.worklogviewer.settings.jackson.WeekdaySettingsSerializer;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

/**
 * Contains all settings for the YouTrack Worklog Viewer
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {

    private WindowSettings windowSettings = new WindowSettings();
    private YouTrackConnectionSettings youTrackConnectionSettings = new YouTrackConnectionSettings();

    private int workHoursADay = 8;
    private ReportTimerange lastUsedReportTimerange = ReportTimerange.THIS_WEEK;
    private boolean loadDataAtStartup = false;
    private boolean showStatistics = true;
    private boolean showAllWorklogs = true;
    private boolean showDecimalHourTimesInExcelReport = false;
    private WeekdaySettings collapseState = new WeekdaySettings(SATURDAY, SUNDAY);
    private WeekdaySettings highlightState = new WeekdaySettings(SATURDAY, SUNDAY);


    /**
     * Settings regarding the main application window
     */
    public WindowSettings getWindowSettings() {
        return windowSettings;
    }

    /**
     * Any settings required to access the YouTrack API
     */
    public YouTrackConnectionSettings getYouTrackConnectionSettings() {
        return youTrackConnectionSettings;
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

    public ReportTimerange getLastUsedReportTimerange() {
        return lastUsedReportTimerange;
    }

    public boolean isLoadDataAtStartup() {
        return loadDataAtStartup;
    }

    public boolean isShowStatistics() {
        return showStatistics;
    }

    public boolean isShowAllWorklogs() {
        return showAllWorklogs;
    }

    public boolean isShowDecimalHourTimesInExcelReport() {
        return showDecimalHourTimesInExcelReport;
    }

    @JsonSerialize(using = WeekdaySettingsSerializer.class)
    @JsonDeserialize(using = WeekdaySettingsDeserializer.class)
    public WeekdaySettings getCollapseState() {
        return collapseState;
    }

    @JsonSerialize(using = WeekdaySettingsSerializer.class)
    @JsonDeserialize(using = WeekdaySettingsDeserializer.class)
    public WeekdaySettings getHighlightState() {
        return highlightState;
    }

    public void setWindowSettings(WindowSettings windowSettings) {
        this.windowSettings = windowSettings;
    }

    public void setYouTrackConnectionSettings(YouTrackConnectionSettings youTrackConnectionSettings) {
        this.youTrackConnectionSettings = youTrackConnectionSettings;
    }

    public void setWorkHoursADay(int workHoursADay) {
        this.workHoursADay = workHoursADay;
    }

    public void setLastUsedReportTimerange(ReportTimerange lastUsedReportTimerange) {
        this.lastUsedReportTimerange = lastUsedReportTimerange;
    }

    public void setLoadDataAtStartup(boolean loadDataAtStartup) {
        this.loadDataAtStartup = loadDataAtStartup;
    }

    public void setShowStatistics(boolean showStatistics) {
        this.showStatistics = showStatistics;
    }

    public void setShowAllWorklogs(boolean showAllWorklogs) {
        this.showAllWorklogs = showAllWorklogs;
    }

    public void setShowDecimalHourTimesInExcelReport(boolean showDecimalHourTimesInExcelReport) {
        this.showDecimalHourTimesInExcelReport = showDecimalHourTimesInExcelReport;
    }

    public void setCollapseState(WeekdaySettings collapseState) {
        this.collapseState = collapseState;
    }

    public void setHighlightState(WeekdaySettings highlightState) {
        this.highlightState = highlightState;
    }

    //    public int getConnectionParametersHashCode() {
//        int result = getHashOrZero(youtrackUrl);
//        result = 31 * result + getHashOrZero(youtrackUsername);
//        result = 31 * result + getHashOrZero(youtrackPassword);
//        result = 31 * result + getHashOrZero(youTrackAuthenticationMethod);
//        result = 31 * result + getHashOrZero(youtrackOAuthServiceId);
//        result = 31 * result + getHashOrZero(youtrackOAuthServiceSecret);
//        result = 31 * result + getHashOrZero(youtrackOAuthHubUrl);
//        result = 31 * result + getHashOrZero(youtrackPermanentToken);
//        return result;
//    }
//
//    private int getHashOrZero(Object o) {
//        if (o == null) return 0;
//        return o.hashCode();
//    }
}
