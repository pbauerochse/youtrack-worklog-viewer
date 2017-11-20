package de.pbauerochse.worklogviewer.youtrack.connector;

/**
 * @author Patrick Bauerochse
 * @since 14.10.15
 */
public enum YouTrackAuthenticationMethod {

    OAUTH2("youtrack.authentication.oauth"),
    HTTP_API("youtrack.authentication.httpapi"),
    PERMANENT_TOKEN("youtrack.authentication.permanenttoken");

    private final String labelKey;
    YouTrackAuthenticationMethod(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getLabelKey() {
        return labelKey;
    }
}
