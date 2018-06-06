package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

/**
 * Settings, that get stored in a properties file
 */
class SettingsImpl implements Settings {

    private int workHoursADay = 8;
    private ReportTimerange lastUsedReportTimerange = ReportTimerange.THIS_WEEK;
    private boolean loadDataAtStartup = false;
    private boolean showStatistics = true;
    private boolean showAllWorklogs = true;
    private boolean showDecimalHourTimesInExcelReport = false;
    private WeekdaySettingsBitMask collapseState = new WeekdaySettingsBitMask(SATURDAY, SUNDAY);
    private WeekdaySettingsBitMask highlightState = new WeekdaySettingsBitMask(SATURDAY, SUNDAY);

    private WindowSettingsImpl windowSettings = new WindowSettingsImpl();
    private YouTrackConnectionSettingsImpl youTrackConnectionSettings = new YouTrackConnectionSettingsImpl();


    @Override
    public WindowSettings getWindowSettings() {
        return windowSettings;
    }

    @Override
    public YouTrackConnectionSettings getYouTrackConnectionSettings() {
        return youTrackConnectionSettings;
    }

    @Override
    public int getWorkHoursADay() {
        return workHoursADay;
    }

    @Override
    public ReportTimerange getLastUsedReportTimerange() {
        return lastUsedReportTimerange;
    }

    @Override
    public boolean isLoadDataAtStartup() {
        return loadDataAtStartup;
    }

    @Override
    public boolean isShowStatistics() {
        return showStatistics;
    }

    @Override
    public boolean isShowAllWorklogs() {
        return showAllWorklogs;
    }

    @Override
    public boolean isShowDecimalHourTimesInExcelReport() {
        return showDecimalHourTimesInExcelReport;
    }

    @Override
    public WeekdaySettingsBitMask getCollapseState() {
        return collapseState;
    }

    @Override
    public WeekdaySettingsBitMask getHighlightState() {
        return highlightState;
    }
}
