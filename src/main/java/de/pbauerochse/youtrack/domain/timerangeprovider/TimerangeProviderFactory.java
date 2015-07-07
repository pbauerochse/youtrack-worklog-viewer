package de.pbauerochse.youtrack.domain.timerangeprovider;

import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.domain.TimerangeProvider;
import de.pbauerochse.youtrack.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class TimerangeProviderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerangeProviderFactory.class);

    private static final Map<ReportTimerange, Class<? extends TimerangeProvider>> REPORT_TIMERANGE_TO_PROVIDER_CLASS = new HashMap<>(4);
    static {
        REPORT_TIMERANGE_TO_PROVIDER_CLASS.put(ReportTimerange.LAST_MONTH, LastMonthTimerangeProvider.class);
        REPORT_TIMERANGE_TO_PROVIDER_CLASS.put(ReportTimerange.LAST_WEEK, LastWeekTimerangeProvider.class);
        REPORT_TIMERANGE_TO_PROVIDER_CLASS.put(ReportTimerange.THIS_MONTH, CurrentMonthTimerangeProvider.class);
        REPORT_TIMERANGE_TO_PROVIDER_CLASS.put(ReportTimerange.THIS_WEEK, CurrentWeekTimerangeProvider.class);
    }

    public static TimerangeProvider getTimerangeProvider(ReportTimerange timerange, LocalDate startDate, LocalDate endDate) {
        Class<? extends TimerangeProvider> timerangeProviderClass = REPORT_TIMERANGE_TO_PROVIDER_CLASS.get(timerange);

        TimerangeProvider timerangeProvider = null;

        if (timerangeProviderClass != null) {
            try {
                timerangeProvider = timerangeProviderClass.newInstance();
            } catch (InstantiationException e) {
                LOGGER.error("Could not instanttiate class {}", timerangeProviderClass.getName(), e);
                throw ExceptionUtil.getIllegalArgumentException("exceptions.internal", e);
            } catch (IllegalAccessException e) {
                LOGGER.error("Could not instanttiate class {}", timerangeProviderClass.getName(), e);
                throw ExceptionUtil.getIllegalArgumentException("exceptions.internal", e);
            }
        }

        if (timerangeProvider == null) {
            timerangeProvider = new CustomTimerangeProvider(startDate, endDate);
        }

        LOGGER.info("Using timerange provider class {}", timerangeProvider.getClass());

        return timerangeProvider;
    }
}
