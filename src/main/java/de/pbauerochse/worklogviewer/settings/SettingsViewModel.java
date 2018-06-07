package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import org.jetbrains.annotations.NotNull;

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

    private final IntegerProperty workhours = new SimpleIntegerProperty();
    private final BooleanProperty showAllWorklogs = new SimpleBooleanProperty();
    private final BooleanProperty showStatistics = new SimpleBooleanProperty();
    private final BooleanProperty loadDataAtStartup = new SimpleBooleanProperty();
    private final BooleanProperty showDecimalsInExcel = new SimpleBooleanProperty();

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

        settings.setWorkHoursADay(workhoursProperty().get());
        settings.setShowAllWorklogs(showAllWorklogsProperty().get());
        settings.setShowStatistics(showStatisticsProperty().get());
        settings.setLoadDataAtStartup(loadDataAtStartupProperty().get());
        settings.setShowDecimalHourTimesInExcelReport(showDecimalsInExcelProperty().get());

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
        workhoursProperty().set(settings.getWorkHoursADay());
        showAllWorklogsProperty().set(settings.isShowAllWorklogs());
        showStatisticsProperty().set(settings.isShowStatistics());
        loadDataAtStartupProperty().set(settings.isLoadDataAtStartup());
        showDecimalsInExcelProperty().set(settings.isShowDecimalHourTimesInExcelReport());
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

    public StringProperty youTrackUrlProperty() {
        return youTrackUrl;
    }

    public YouTrackVersion getYouTrackVersion() {
        return youTrackVersion.get();
    }

    public ObjectProperty<YouTrackVersion> youTrackVersionProperty() {
        return youTrackVersion;
    }

    public StringProperty youTrackUsernameProperty() {
        return youTrackUsername;
    }

    public StringProperty youTrackPermanentTokenProperty() {
        return youTrackPermanentToken;
    }

    public IntegerProperty workhoursProperty() {
        return workhours;
    }

    public BooleanProperty showAllWorklogsProperty() {
        return showAllWorklogs;
    }

    public BooleanProperty showStatisticsProperty() {
        return showStatistics;
    }

    public BooleanProperty loadDataAtStartupProperty() {
        return loadDataAtStartup;
    }

    public BooleanProperty showDecimalsInExcelProperty() {
        return showDecimalsInExcel;
    }

    public BooleanProperty collapseStateMondayProperty() {
        return collapseStateMonday;
    }

    public BooleanProperty collapseStateTuesdayProperty() {
        return collapseStateTuesday;
    }

    public BooleanProperty collapseStateWednesdayProperty() {
        return collapseStateWednesday;
    }

    public BooleanProperty collapseStateThursdayProperty() {
        return collapseStateThursday;
    }

    public BooleanProperty collapseStateFridayProperty() {
        return collapseStateFriday;
    }

    public BooleanProperty collapseStateSaturdayProperty() {
        return collapseStateSaturday;
    }

    public BooleanProperty collapseStateSundayProperty() {
        return collapseStateSunday;
    }

    public BooleanProperty highlightStateMondayProperty() {
        return highlightStateMonday;
    }

    public BooleanProperty highlightStateTuesdayProperty() {
        return highlightStateTuesday;
    }

    public BooleanProperty highlightStateWednesdayProperty() {
        return highlightStateWednesday;
    }

    public BooleanProperty highlightStateThursdayProperty() {
        return highlightStateThursday;
    }

    public BooleanProperty highlightStateFridayProperty() {
        return highlightStateFriday;
    }

    public BooleanProperty highlightStateSaturdayProperty() {
        return highlightStateSaturday;
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
