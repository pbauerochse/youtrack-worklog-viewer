package de.pbauerochse.worklogviewer.youtrack.domain;

/**
 * A category which can be used to group
 * tasks within the time report
 */
@Deprecated
public interface GroupByCategory {

    String getId();
    String getName();

    default boolean isValidYouTrackCategory() {
        return true;
    }

}
