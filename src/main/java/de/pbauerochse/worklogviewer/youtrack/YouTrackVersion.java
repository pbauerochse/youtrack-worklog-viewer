package de.pbauerochse.worklogviewer.youtrack;

public enum YouTrackVersion {

    PRE_2017("youtrack.version.olderOrEqual20173"),
    POST_2017("youtrack.version.newerOrEqual20174"),
    POST_2018("youtrack.version.newerOrEqual20181");

    private final String labelKey;

    YouTrackVersion(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getLabelKey() {
        return labelKey;
    }
}
