package de.pbauerochse.youtrack.fx.tasks;

import de.pbauerochse.youtrack.domain.TimerangeProvider;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class FetchTimereportContext {

    private TimerangeProvider timerangeProvider;

    public FetchTimereportContext(TimerangeProvider timerangeProvider) {
        this.timerangeProvider = timerangeProvider;
    }

    public TimerangeProvider getTimerangeProvider() {
        return timerangeProvider;
    }
}
