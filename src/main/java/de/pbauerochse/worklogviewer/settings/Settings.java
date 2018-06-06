package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;

/**
 * Contains all settings for the YouTrack Worklog Viewer
 */
public interface Settings {

    /**
     * Settings regarding the main application window
     */
    WindowSettings getWindowSettings();

    /**
     * Any settings required to access the YouTrack API
     */
    YouTrackConnectionSettings getYouTrackConnectionSettings();

    /**
     * The amount of hours the user has to work on
     * any given day. Determines, how the YouTrack
     * times are being displayed:
     * <p>
     * - 9h booked hours on a 8h workday -> 1d 1h
     * - 9h booked hours on a 6h workday -> 1d 3h
     * - 9h booked hours on a 9h workday -> 1d
     */
    int getWorkHoursADay();

    ReportTimerange getLastUsedReportTimerange();

    boolean isLoadDataAtStartup();

    boolean isShowStatistics();

    boolean isShowAllWorklogs();

    boolean isShowDecimalHourTimesInExcelReport();

    WeekdaySettingsBitMask getCollapseState();

    WeekdaySettingsBitMask getHighlightState();

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
