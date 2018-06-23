package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.connector.YouTrackVersion;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.fx.Theme;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.function.Consumer;

import static java.time.DayOfWeek.*;

/**
 * Java FX Model for the settings screen
 */
public class SettingsViewModel {

    private final Settings settings;

    private final StringProperty youTrackUrl = new SimpleStringProperty();
    private final ObjectProperty<YouTrackVersion> youTrackVersion = new SimpleObjectProperty<>();
    private final StringProperty youTrackUsername = new SimpleStringProperty();
    private final StringProperty youTrackPermanentToken = new SimpleStringProperty();

    private final ObjectProperty<Theme> theme = new SimpleObjectProperty<>();
    private final IntegerProperty workhours = new SimpleIntegerProperty();
    private final BooleanProperty showAllWorklogs = new SimpleBooleanProperty();
    private final BooleanProperty showStatistics = new SimpleBooleanProperty();
    private final BooleanProperty loadDataAtStartup = new SimpleBooleanProperty();
    private final BooleanProperty showDecimalsInExcel = new SimpleBooleanProperty();
    private final ObjectProperty<ReportTimerange> lastUsedReportTimerange = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private final StringProperty lastUsedGroupByCategoryId = new SimpleStringProperty();

    private final BooleanProperty collapseStateMonday = new SimpleBooleanProperty();
    private final BooleanProperty collapseStateTuesday = new SimpleBooleanProperty();
    private final BooleanProperty collapseStateWednesday = new SimpleBooleanProperty();
    private final BooleanProperty collapseStateThursday = new SimpleBooleanProperty();
    private final BooleanProperty collapseStateFriday = new SimpleBooleanProperty();
    private final BooleanProperty collapseStateSaturday = new SimpleBooleanProperty();
    private final BooleanProperty collapseStateSunday = new SimpleBooleanProperty();

    private final BooleanProperty highlightStateMonday = new SimpleBooleanProperty();
    private final BooleanProperty highlightStateTuesday = new SimpleBooleanProperty();
    private final BooleanProperty highlightStateWednesday = new SimpleBooleanProperty();
    private final BooleanProperty highlightStateThursday = new SimpleBooleanProperty();
    private final BooleanProperty highlightStateFriday = new SimpleBooleanProperty();
    private final BooleanProperty highlightStateSaturday = new SimpleBooleanProperty();
    private final BooleanProperty highlightStateSunday = new SimpleBooleanProperty();

    private final BooleanBinding hasMissingConnectionSettings = getHasMissingConnectionSettingsBinding();

    SettingsViewModel(@NotNull Settings settings) {
        this.settings = settings;
        this.applyPropertiesFromSettings();
        this.bindAutoUpdatingProperties();
    }

    private BooleanBinding getHasMissingConnectionSettingsBinding() {
        return youTrackUrl.isEmpty()
                .or(youTrackVersion.isNull())
                .or(youTrackUsername.isEmpty())
                .or(youTrackPermanentToken.isEmpty());
    }

    public void saveChanges() {
        settings.getYouTrackConnectionSettings().setUrl(youTrackUrlProperty().get());
        settings.getYouTrackConnectionSettings().setVersion(getYouTrackVersion());
        settings.getYouTrackConnectionSettings().setUsername(youTrackUsernameProperty().get());
        settings.getYouTrackConnectionSettings().setPermanentToken(youTrackPermanentTokenProperty().get());

        settings.setTheme(themeProperty().get());
        settings.setWorkHoursADay(workhoursProperty().get());
        settings.setShowAllWorklogs(showAllWorklogsProperty().get());
        settings.setShowStatistics(showStatisticsProperty().get());
        settings.setLoadDataAtStartup(loadDataAtStartupProperty().get());
        settings.setShowDecimalHourTimesInExcelReport(showDecimalsInExcelProperty().get());
        settings.setLastUsedReportTimerange(lastUsedReportTimerangeProperty().get());
        settings.setStartDate(startDateProperty().get());
        settings.setEndDate(endDateProperty().get());
        settings.setLastUsedGroupByCategoryId(lastUsedGroupByCategoryIdProperty().get());

        settings.getCollapseState().set(MONDAY, collapseStateMondayProperty().get());
        settings.getCollapseState().set(TUESDAY, collapseStateTuesdayProperty().get());
        settings.getCollapseState().set(WEDNESDAY, collapseStateWednesdayProperty().get());
        settings.getCollapseState().set(THURSDAY, collapseStateThursdayProperty().get());
        settings.getCollapseState().set(FRIDAY, collapseStateFridayProperty().get());
        settings.getCollapseState().set(SATURDAY, collapseStateSaturdayProperty().get());
        settings.getCollapseState().set(SUNDAY, collapseStateSundayProperty().get());

        settings.getHighlightState().set(MONDAY, highlightStateMondayProperty().get());
        settings.getHighlightState().set(TUESDAY, highlightStateTuesdayProperty().get());
        settings.getHighlightState().set(WEDNESDAY, highlightStateWednesdayProperty().get());
        settings.getHighlightState().set(THURSDAY, highlightStateThursdayProperty().get());
        settings.getHighlightState().set(FRIDAY, highlightStateFridayProperty().get());
        settings.getHighlightState().set(SATURDAY, highlightStateSaturdayProperty().get());
        settings.getHighlightState().set(SUNDAY, highlightStateSundayProperty().get());

        SettingsUtil.saveSettings();
    }

    public void discardChanges() {
        applyPropertiesFromSettings();
    }

    private void applyPropertiesFromSettings() {
        youTrackUrlProperty().set(settings.getYouTrackConnectionSettings().getUrl());
        youTrackVersionProperty().set(settings.getYouTrackConnectionSettings().getVersion());
        youTrackUsernameProperty().set(settings.getYouTrackConnectionSettings().getUsername());
        youTrackPermanentTokenProperty().set(settings.getYouTrackConnectionSettings().getPermanentToken());

        themeProperty().set(settings.getTheme());
        workhoursProperty().set(settings.getWorkHoursADay());
        showAllWorklogsProperty().set(settings.isShowAllWorklogs());
        showStatisticsProperty().set(settings.isShowStatistics());
        loadDataAtStartupProperty().set(settings.isLoadDataAtStartup());
        showDecimalsInExcelProperty().set(settings.isShowDecimalHourTimesInExcelReport());
        lastUsedReportTimerangeProperty().set(settings.getLastUsedReportTimerange());
        startDateProperty().set(settings.getStartDate());
        endDateProperty().set(settings.getEndDate());
        lastUsedGroupByCategoryIdProperty().set(settings.getLastUsedGroupByCategoryId());

        collapseStateMondayProperty().set(settings.getCollapseState().isSet(MONDAY));
        collapseStateTuesdayProperty().set(settings.getCollapseState().isSet(TUESDAY));
        collapseStateWednesdayProperty().set(settings.getCollapseState().isSet(WEDNESDAY));
        collapseStateThursdayProperty().set(settings.getCollapseState().isSet(THURSDAY));
        collapseStateFridayProperty().set(settings.getCollapseState().isSet(FRIDAY));
        collapseStateSaturdayProperty().set(settings.getCollapseState().isSet(SATURDAY));
        collapseStateSundayProperty().set(settings.getCollapseState().isSet(SUNDAY));

        highlightStateMondayProperty().set(settings.getHighlightState().isSet(MONDAY));
        highlightStateTuesdayProperty().set(settings.getHighlightState().isSet(TUESDAY));
        highlightStateWednesdayProperty().set(settings.getHighlightState().isSet(WEDNESDAY));
        highlightStateThursdayProperty().set(settings.getHighlightState().isSet(THURSDAY));
        highlightStateFridayProperty().set(settings.getHighlightState().isSet(FRIDAY));
        highlightStateSaturdayProperty().set(settings.getHighlightState().isSet(SATURDAY));
        highlightStateSundayProperty().set(settings.getHighlightState().isSet(SUNDAY));
    }

    /**
     * These settings are applied to the persistent settings
     * object whenever they are changed. In general, those are
     * the application state properties, that are not set
     * in the settings view.
     */
    private void bindAutoUpdatingProperties() {
        lastUsedReportTimerangeProperty().addListener(invokeSetter(settings::setLastUsedReportTimerange));
        lastUsedGroupByCategoryIdProperty().addListener(invokeSetter(settings::setLastUsedGroupByCategoryId));
        startDateProperty().addListener(invokeSetter(settings::setStartDate));
        endDateProperty().addListener(invokeSetter(settings::setEndDate));
    }

    private <T> ChangeListener<T> invokeSetter(Consumer<T> setter) {
        return ((observable, oldValue, newValue) -> setter.accept(newValue));
    }

    public String getYouTrackUrl() {
        return youTrackUrl.get();
    }

    public StringProperty youTrackUrlProperty() {
        return youTrackUrl;
    }

    public YouTrackVersion getYouTrackVersion() {
        return youTrackVersion.get();
    }

    public ObjectProperty<YouTrackVersion> youTrackVersionProperty() {
        return youTrackVersion;
    }

    public String getYouTrackUsername() {
        return youTrackUsername.get();
    }

    public StringProperty youTrackUsernameProperty() {
        return youTrackUsername;
    }

    public String getYouTrackPermanentToken() {
        return youTrackPermanentToken.get();
    }

    public StringProperty youTrackPermanentTokenProperty() {
        return youTrackPermanentToken;
    }

    public Theme getTheme() {
        return theme.get();
    }

    public ObjectProperty<Theme> themeProperty() {
        return theme;
    }

    public int getWorkhours() {
        return workhours.get();
    }

    public IntegerProperty workhoursProperty() {
        return workhours;
    }

    public boolean isShowAllWorklogs() {
        return showAllWorklogs.get();
    }

    public BooleanProperty showAllWorklogsProperty() {
        return showAllWorklogs;
    }

    public boolean isShowStatistics() {
        return showStatistics.get();
    }

    public BooleanProperty showStatisticsProperty() {
        return showStatistics;
    }

    public boolean isLoadDataAtStartup() {
        return loadDataAtStartup.get();
    }

    public BooleanProperty loadDataAtStartupProperty() {
        return loadDataAtStartup;
    }

    public boolean isShowDecimalsInExcel() {
        return showDecimalsInExcel.get();
    }

    public BooleanProperty showDecimalsInExcelProperty() {
        return showDecimalsInExcel;
    }

    public ReportTimerange getLastUsedReportTimerange() {
        return lastUsedReportTimerange.get();
    }

    public ObjectProperty<ReportTimerange> lastUsedReportTimerangeProperty() {
        return lastUsedReportTimerange;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public String getLastUsedGroupByCategoryId() {
        return lastUsedGroupByCategoryId.get();
    }

    public StringProperty lastUsedGroupByCategoryIdProperty() {
        return lastUsedGroupByCategoryId;
    }

    public boolean isCollapseStateMonday() {
        return collapseStateMonday.get();
    }

    public BooleanProperty collapseStateMondayProperty() {
        return collapseStateMonday;
    }

    public boolean isCollapseStateTuesday() {
        return collapseStateTuesday.get();
    }

    public BooleanProperty collapseStateTuesdayProperty() {
        return collapseStateTuesday;
    }

    public boolean isCollapseStateWednesday() {
        return collapseStateWednesday.get();
    }

    public BooleanProperty collapseStateWednesdayProperty() {
        return collapseStateWednesday;
    }

    public boolean isCollapseStateThursday() {
        return collapseStateThursday.get();
    }

    public BooleanProperty collapseStateThursdayProperty() {
        return collapseStateThursday;
    }

    public boolean isCollapseStateFriday() {
        return collapseStateFriday.get();
    }

    public BooleanProperty collapseStateFridayProperty() {
        return collapseStateFriday;
    }

    public boolean isCollapseStateSaturday() {
        return collapseStateSaturday.get();
    }

    public BooleanProperty collapseStateSaturdayProperty() {
        return collapseStateSaturday;
    }

    public boolean isCollapseStateSunday() {
        return collapseStateSunday.get();
    }

    public BooleanProperty collapseStateSundayProperty() {
        return collapseStateSunday;
    }

    public boolean isHighlightStateMonday() {
        return highlightStateMonday.get();
    }

    public BooleanProperty highlightStateMondayProperty() {
        return highlightStateMonday;
    }

    public boolean isHighlightStateTuesday() {
        return highlightStateTuesday.get();
    }

    public BooleanProperty highlightStateTuesdayProperty() {
        return highlightStateTuesday;
    }

    public boolean isHighlightStateWednesday() {
        return highlightStateWednesday.get();
    }

    public BooleanProperty highlightStateWednesdayProperty() {
        return highlightStateWednesday;
    }

    public boolean isHighlightStateThursday() {
        return highlightStateThursday.get();
    }

    public BooleanProperty highlightStateThursdayProperty() {
        return highlightStateThursday;
    }

    public boolean isHighlightStateFriday() {
        return highlightStateFriday.get();
    }

    public BooleanProperty highlightStateFridayProperty() {
        return highlightStateFriday;
    }

    public boolean isHighlightStateSaturday() {
        return highlightStateSaturday.get();
    }

    public BooleanProperty highlightStateSaturdayProperty() {
        return highlightStateSaturday;
    }

    public boolean isHighlightStateSunday() {
        return highlightStateSunday.get();
    }

    public BooleanProperty highlightStateSundayProperty() {
        return highlightStateSunday;
    }

    public Boolean getHasMissingConnectionSettings() {
        return hasMissingConnectionSettings.get();
    }

    public BooleanBinding hasMissingConnectionSettingsProperty() {
        return hasMissingConnectionSettings;
    }
}
