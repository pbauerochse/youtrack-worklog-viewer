package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;

import static java.time.DayOfWeek.*;

public class SettingsViewModel {

    private final StringProperty youTrackUrl = new SimpleStringProperty();
    private final ObjectProperty<YouTrackVersion> youTrackVersion = new SimpleObjectProperty<>();
    private final ObjectProperty<YouTrackAuthenticationMethod> youTrackAuthenticationMethod = new SimpleObjectProperty<>();
    private final StringProperty youTrackUsername = new SimpleStringProperty();
    private final StringProperty youTrackPassword = new SimpleStringProperty();
    private final StringProperty youTrackHubUrl = new SimpleStringProperty();
    private final StringProperty youTrackOAuth2ServiceId = new SimpleStringProperty();
    private final StringProperty youTrackOAuth2ServiceSecret = new SimpleStringProperty();
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

    private final BooleanBinding requiresUsernamePassword = youTrackAuthenticationMethod.isEqualTo(YouTrackAuthenticationMethod.HTTP_API).or(youTrackAuthenticationMethod.isEqualTo(YouTrackAuthenticationMethod.OAUTH2));
    private final BooleanBinding requiresOAuthSettings = youTrackAuthenticationMethod.isEqualTo(YouTrackAuthenticationMethod.OAUTH2);
    private final BooleanBinding requiresPermanentToken = youTrackAuthenticationMethod.isEqualTo(YouTrackAuthenticationMethod.PERMANENT_TOKEN);

    private final BooleanBinding hasValidConnectionParameters = getValidConnectionSettingsBinding();

    private BooleanBinding getValidConnectionSettingsBinding() {
        BooleanBinding hasValidUsernamePasswordCombination = youTrackUsername.isNotEmpty().and(youTrackPassword.isNotEmpty());
        BooleanBinding hasValidOAuth2Combination = hasValidUsernamePasswordCombination
                .and(youTrackOAuth2ServiceId.isNotEmpty())
                .and(youTrackOAuth2ServiceSecret.isNotEmpty())
                .and(youTrackHubUrl.isNotEmpty());
        BooleanBinding hasValidPermanentTokenCombination = youTrackPermanentToken.isNotEmpty();

        return youTrackUrl.isNotEmpty()
                .and(
                        (requiresUsernamePassword.and(hasValidUsernamePasswordCombination))
                                .or(requiresOAuthSettings.and(hasValidOAuth2Combination))
                                .or(requiresPermanentToken.and(hasValidPermanentTokenCombination))
                );
    }

    SettingsViewModel() {
        loadPropertiesFromSettings();
    }

    public void saveChanges() {
        Settings settings = SettingsUtil.getSettings();

        settings.setYoutrackUrl(getYouTrackUrl());
        settings.setYouTrackVersion(getYouTrackVersion());
        settings.setYouTrackAuthenticationMethod(getYouTrackAuthenticationMethod());
        settings.setYoutrackUsername(getYouTrackUsername());
        settings.setYoutrackPassword(getYouTrackPassword());
        settings.setYoutrackOAuthHubUrl(getYouTrackHubUrl());
        settings.setYoutrackOAuthServiceId(getYouTrackOAuth2ServiceId());
        settings.setYoutrackOAuthServiceSecret(getYouTrackOAuth2ServiceSecret());
        settings.setYoutrackPermanentToken(getYouTrackPermanentToken());

        settings.setWorkHoursADay(getWorkhours());
        settings.setShowAllWorklogs(isShowAllWorklogs());
        settings.setShowStatistics(isShowStatistics());
        settings.setLoadDataAtStartup(isLoadDataAtStartup());
        settings.setShowDecimalHourTimesInExcelReport(isShowDecimalsInExcel());

        settings.setCollapseState(MONDAY, isCollapseStateMonday());
        settings.setCollapseState(TUESDAY, isCollapseStateTuesday());
        settings.setCollapseState(WEDNESDAY, isCollapseStateWednesday());
        settings.setCollapseState(THURSDAY, isCollapseStateThursday());
        settings.setCollapseState(FRIDAY, isCollapseStateFriday());
        settings.setCollapseState(SATURDAY, isCollapseStateSaturday());
        settings.setCollapseState(SUNDAY, isCollapseStateSunday());

        settings.setHighlightState(MONDAY, isHighlightStateMonday());
        settings.setHighlightState(TUESDAY, isHighlightStateTuesday());
        settings.setHighlightState(WEDNESDAY, isHighlightStateWednesday());
        settings.setHighlightState(THURSDAY, isHighlightStateThursday());
        settings.setHighlightState(FRIDAY, isHighlightStateFriday());
        settings.setHighlightState(SATURDAY, isHighlightStateSaturday());
        settings.setHighlightState(SUNDAY, isHighlightStateSunday());

        SettingsUtil.saveSettings();
    }

    public void discardChanges() {
        loadPropertiesFromSettings();
    }

    private void loadPropertiesFromSettings() {
        Settings settings = SettingsUtil.getSettings();

        setYouTrackUrl(settings.getYoutrackUrl());
        setYouTrackVersion(settings.getYouTrackVersion());

        setYouTrackAuthenticationMethod(settings.getYouTrackAuthenticationMethod());
        setYouTrackUsername(settings.getYoutrackUsername());
        setYouTrackPassword(settings.getYoutrackPassword());
        setYouTrackHubUrl(settings.getYoutrackOAuthHubUrl());
        setYouTrackOAuth2ServiceId(settings.getYoutrackOAuthServiceId());
        setYouTrackOAuth2ServiceSecret(settings.getYoutrackOAuthServiceSecret());
        setYouTrackPermanentToken(settings.getYoutrackPermanentToken());

        setWorkhours(settings.getWorkHoursADay());
        setShowAllWorklogs(settings.isShowAllWorklogs());
        setShowStatistics(settings.isShowStatistics());
        setLoadDataAtStartup(settings.isLoadDataAtStartup());
        setShowDecimalsInExcel(settings.isShowDecimalHourTimesInExcelReport());

        setCollapseStateMonday(settings.hasCollapseState(MONDAY));
        setCollapseStateTuesday(settings.hasCollapseState(TUESDAY));
        setCollapseStateWednesday(settings.hasCollapseState(WEDNESDAY));
        setCollapseStateThursday(settings.hasCollapseState(THURSDAY));
        setCollapseStateFriday(settings.hasCollapseState(FRIDAY));
        setCollapseStateSaturday(settings.hasCollapseState(SATURDAY));
        setCollapseStateSunday(settings.hasCollapseState(SUNDAY));

        setHighlightStateMonday(settings.hasHighlightState(MONDAY));
        setHighlightStateTuesday(settings.hasHighlightState(TUESDAY));
        setHighlightStateWednesday(settings.hasHighlightState(WEDNESDAY));
        setHighlightStateThursday(settings.hasHighlightState(THURSDAY));
        setHighlightStateFriday(settings.hasHighlightState(FRIDAY));
        setHighlightStateSaturday(settings.hasHighlightState(SATURDAY));
        setHighlightStateSunday(settings.hasHighlightState(SUNDAY));
    }

    public String getYouTrackUrl() {
        return youTrackUrl.get();
    }

    public StringProperty youTrackUrlProperty() {
        return youTrackUrl;
    }

    public void setYouTrackUrl(String youTrackUrl) {
        this.youTrackUrl.set(youTrackUrl);
    }

    public YouTrackVersion getYouTrackVersion() {
        return youTrackVersion.get();
    }

    public ObjectProperty<YouTrackVersion> youTrackVersionProperty() {
        return youTrackVersion;
    }

    public void setYouTrackVersion(YouTrackVersion youTrackVersion) {
        this.youTrackVersion.set(youTrackVersion);
    }

    public YouTrackAuthenticationMethod getYouTrackAuthenticationMethod() {
        return youTrackAuthenticationMethod.get();
    }

    public ObjectProperty<YouTrackAuthenticationMethod> youTrackAuthenticationMethodProperty() {
        return youTrackAuthenticationMethod;
    }

    public void setYouTrackAuthenticationMethod(YouTrackAuthenticationMethod youTrackAuthenticationMethod) {
        this.youTrackAuthenticationMethod.set(youTrackAuthenticationMethod);
    }

    public String getYouTrackUsername() {
        return youTrackUsername.get();
    }

    public StringProperty youTrackUsernameProperty() {
        return youTrackUsername;
    }

    public void setYouTrackUsername(String youTrackUsername) {
        this.youTrackUsername.set(youTrackUsername);
    }

    public String getYouTrackPassword() {
        return youTrackPassword.get();
    }

    public StringProperty youTrackPasswordProperty() {
        return youTrackPassword;
    }

    public void setYouTrackPassword(String youTrackPassword) {
        this.youTrackPassword.set(youTrackPassword);
    }

    public String getYouTrackHubUrl() {
        return youTrackHubUrl.get();
    }

    public StringProperty youTrackHubUrlProperty() {
        return youTrackHubUrl;
    }

    public void setYouTrackHubUrl(String youTrackHubUrl) {
        this.youTrackHubUrl.set(youTrackHubUrl);
    }

    public String getYouTrackOAuth2ServiceId() {
        return youTrackOAuth2ServiceId.get();
    }

    public StringProperty youTrackOAuth2ServiceIdProperty() {
        return youTrackOAuth2ServiceId;
    }

    public void setYouTrackOAuth2ServiceId(String youTrackOAuth2ServiceId) {
        this.youTrackOAuth2ServiceId.set(youTrackOAuth2ServiceId);
    }

    public String getYouTrackOAuth2ServiceSecret() {
        return youTrackOAuth2ServiceSecret.get();
    }

    public StringProperty youTrackOAuth2ServiceSecretProperty() {
        return youTrackOAuth2ServiceSecret;
    }

    public void setYouTrackOAuth2ServiceSecret(String youTrackOAuth2ServiceSecret) {
        this.youTrackOAuth2ServiceSecret.set(youTrackOAuth2ServiceSecret);
    }

    public String getYouTrackPermanentToken() {
        return youTrackPermanentToken.get();
    }

    public StringProperty youTrackPermanentTokenProperty() {
        return youTrackPermanentToken;
    }

    public void setYouTrackPermanentToken(String youTrackPermanentToken) {
        this.youTrackPermanentToken.set(youTrackPermanentToken);
    }

    public int getWorkhours() {
        return workhours.get();
    }

    public IntegerProperty workhoursProperty() {
        return workhours;
    }

    public void setWorkhours(int workhours) {
        this.workhours.set(workhours);
    }

    public boolean isShowAllWorklogs() {
        return showAllWorklogs.get();
    }

    public BooleanProperty showAllWorklogsProperty() {
        return showAllWorklogs;
    }

    public void setShowAllWorklogs(boolean showAllWorklogs) {
        this.showAllWorklogs.set(showAllWorklogs);
    }

    public boolean isShowStatistics() {
        return showStatistics.get();
    }

    public BooleanProperty showStatisticsProperty() {
        return showStatistics;
    }

    public void setShowStatistics(boolean showStatistics) {
        this.showStatistics.set(showStatistics);
    }

    public boolean isLoadDataAtStartup() {
        return loadDataAtStartup.get();
    }

    public BooleanProperty loadDataAtStartupProperty() {
        return loadDataAtStartup;
    }

    public void setLoadDataAtStartup(boolean loadDataAtStartup) {
        this.loadDataAtStartup.set(loadDataAtStartup);
    }

    public boolean isShowDecimalsInExcel() {
        return showDecimalsInExcel.get();
    }

    public BooleanProperty showDecimalsInExcelProperty() {
        return showDecimalsInExcel;
    }

    public void setShowDecimalsInExcel(boolean showDecimalsInExcel) {
        this.showDecimalsInExcel.set(showDecimalsInExcel);
    }

    public boolean isCollapseStateMonday() {
        return collapseStateMonday.get();
    }

    public BooleanProperty collapseStateMondayProperty() {
        return collapseStateMonday;
    }

    public void setCollapseStateMonday(boolean collapseStateMonday) {
        this.collapseStateMonday.set(collapseStateMonday);
    }

    public boolean isCollapseStateTuesday() {
        return collapseStateTuesday.get();
    }

    public BooleanProperty collapseStateTuesdayProperty() {
        return collapseStateTuesday;
    }

    public void setCollapseStateTuesday(boolean collapseStateTuesday) {
        this.collapseStateTuesday.set(collapseStateTuesday);
    }

    public boolean isCollapseStateWednesday() {
        return collapseStateWednesday.get();
    }

    public BooleanProperty collapseStateWednesdayProperty() {
        return collapseStateWednesday;
    }

    public void setCollapseStateWednesday(boolean collapseStateWednesday) {
        this.collapseStateWednesday.set(collapseStateWednesday);
    }

    public boolean isCollapseStateThursday() {
        return collapseStateThursday.get();
    }

    public BooleanProperty collapseStateThursdayProperty() {
        return collapseStateThursday;
    }

    public void setCollapseStateThursday(boolean collapseStateThursday) {
        this.collapseStateThursday.set(collapseStateThursday);
    }

    public boolean isCollapseStateFriday() {
        return collapseStateFriday.get();
    }

    public BooleanProperty collapseStateFridayProperty() {
        return collapseStateFriday;
    }

    public void setCollapseStateFriday(boolean collapseStateFriday) {
        this.collapseStateFriday.set(collapseStateFriday);
    }

    public boolean isCollapseStateSaturday() {
        return collapseStateSaturday.get();
    }

    public BooleanProperty collapseStateSaturdayProperty() {
        return collapseStateSaturday;
    }

    public void setCollapseStateSaturday(boolean collapseStateSaturday) {
        this.collapseStateSaturday.set(collapseStateSaturday);
    }

    public boolean isCollapseStateSunday() {
        return collapseStateSunday.get();
    }

    public BooleanProperty collapseStateSundayProperty() {
        return collapseStateSunday;
    }

    public void setCollapseStateSunday(boolean collapseStateSunday) {
        this.collapseStateSunday.set(collapseStateSunday);
    }

    public boolean isHighlightStateMonday() {
        return highlightStateMonday.get();
    }

    public BooleanProperty highlightStateMondayProperty() {
        return highlightStateMonday;
    }

    public void setHighlightStateMonday(boolean highlightStateMonday) {
        this.highlightStateMonday.set(highlightStateMonday);
    }

    public boolean isHighlightStateTuesday() {
        return highlightStateTuesday.get();
    }

    public BooleanProperty highlightStateTuesdayProperty() {
        return highlightStateTuesday;
    }

    public void setHighlightStateTuesday(boolean highlightStateTuesday) {
        this.highlightStateTuesday.set(highlightStateTuesday);
    }

    public boolean isHighlightStateWednesday() {
        return highlightStateWednesday.get();
    }

    public BooleanProperty highlightStateWednesdayProperty() {
        return highlightStateWednesday;
    }

    public void setHighlightStateWednesday(boolean highlightStateWednesday) {
        this.highlightStateWednesday.set(highlightStateWednesday);
    }

    public boolean isHighlightStateThursday() {
        return highlightStateThursday.get();
    }

    public BooleanProperty highlightStateThursdayProperty() {
        return highlightStateThursday;
    }

    public void setHighlightStateThursday(boolean highlightStateThursday) {
        this.highlightStateThursday.set(highlightStateThursday);
    }

    public boolean isHighlightStateFriday() {
        return highlightStateFriday.get();
    }

    public BooleanProperty highlightStateFridayProperty() {
        return highlightStateFriday;
    }

    public void setHighlightStateFriday(boolean highlightStateFriday) {
        this.highlightStateFriday.set(highlightStateFriday);
    }

    public boolean isHighlightStateSaturday() {
        return highlightStateSaturday.get();
    }

    public BooleanProperty highlightStateSaturdayProperty() {
        return highlightStateSaturday;
    }

    public void setHighlightStateSaturday(boolean highlightStateSaturday) {
        this.highlightStateSaturday.set(highlightStateSaturday);
    }

    public boolean isHighlightStateSunday() {
        return highlightStateSunday.get();
    }

    public BooleanProperty highlightStateSundayProperty() {
        return highlightStateSunday;
    }

    public void setHighlightStateSunday(boolean highlightStateSunday) {
        this.highlightStateSunday.set(highlightStateSunday);
    }

    public Boolean getHasValidConnectionParameters() {
        return hasValidConnectionParameters.get();
    }

    public BooleanBinding hasValidConnectionParametersProperty() {
        return hasValidConnectionParameters;
    }

    public Boolean getRequiresUsernamePassword() {
        return requiresUsernamePassword.get();
    }

    public BooleanBinding requiresUsernamePasswordProperty() {
        return requiresUsernamePassword;
    }

    public Boolean getRequiresOAuthSettings() {
        return requiresOAuthSettings.get();
    }

    public BooleanBinding requiresOAuthSettingsProperty() {
        return requiresOAuthSettings;
    }

    public Boolean getRequiresPermanentToken() {
        return requiresPermanentToken.get();
    }

    public BooleanBinding requiresPermanentTokenProperty() {
        return requiresPermanentToken;
    }
}
