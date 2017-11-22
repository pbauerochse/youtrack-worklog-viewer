package de.pbauerochse.worklogviewer.youtrack;

public enum YouTrackVersion {

    PRE_2017("youtrack.version.olderOrEqual20173"),
    POST_2017("youtrack.version.newerOrEqual20174");

    private final String labelKey;

    YouTrackVersion(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getLabelKey() {
        return labelKey;
    }
}
