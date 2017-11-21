package de.pbauerochse.worklogviewer.youtrack;

public enum YouTrackVersion {

    PRE_2017("youtrack.version.pre2017"),
    POST_2017("youtrack.version.post2017");

    private final String labelKey;

    YouTrackVersion(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getLabelKey() {
        return labelKey;
    }
}
