package de.pbauerochse.worklogviewer.fx;

import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;

public class SettingsViewModel {

    private StringProperty youTrackUrl;
    private ObjectProperty<YouTrackVersion> youTrackVersion;
    private ObjectProperty<YouTrackAuthenticationMethod> youTrackAuthenticationMethod;

    private StringProperty youTrackUsername;
    private StringProperty youTrackPassword;

    private StringProperty youTrackHubUrl;
    private StringProperty youTrackOAuth2ServiceId;
    private StringProperty youTrackOAuth2ServiceSecret;

    private StringProperty youTrackPermanentToken;

    @SuppressWarnings("unchecked")
    public SettingsViewModel(SettingsUtil.Settings settings) {
        try {
            youTrackUrl = new JavaBeanStringPropertyBuilder().bean(settings).name("youtrackUrl").build();
            youTrackVersion = new JavaBeanObjectPropertyBuilder<YouTrackVersion>().bean(settings).name("youTrackVersion").build();
            youTrackAuthenticationMethod = new JavaBeanObjectPropertyBuilder<YouTrackAuthenticationMethod>().bean(settings).name("youTrackAuthenticationMethod").build();

            youTrackUsername = new JavaBeanStringPropertyBuilder().bean(settings).name("youtrackUsername").build();
            youTrackPassword = new JavaBeanStringPropertyBuilder().bean(settings).name("youtrackPassword").build();

            youTrackHubUrl = new JavaBeanStringPropertyBuilder().bean(settings).name("youtrackOAuthHubUrl").build();
            youTrackOAuth2ServiceId = new JavaBeanStringPropertyBuilder().bean(settings).name("youtrackOAuthServiceId").build();
            youTrackOAuth2ServiceSecret = new JavaBeanStringPropertyBuilder().bean(settings).name("youtrackOAuthServiceSecret").build();

            youTrackPermanentToken = new JavaBeanStringPropertyBuilder().bean(settings).name("youtrackPermanentToken").build();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Can not bind properties to Settings", e);
        }
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
}
