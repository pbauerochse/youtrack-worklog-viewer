package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * TimerangeProvider for custom provided dates
 */
public class CustomTimerangeProvider extends BaseTimerangeProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CustomTimerangeProvider.class);

    CustomTimerangeProvider(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        validateDates();
    }

    @Override
    public ReportTimerange getReportTimerange() {
        return ReportTimerange.CUSTOM;
    }

    private void validateDates() {
        if (startDate == null || endDate == null) {
            LOG.warn("Startdate and / or enddate was null");
            throw ExceptionUtil.getIllegalArgumentException("exceptions.timerange.datesrequired");
        } else if (startDate.isAfter(endDate)) {
            LOG.warn("Startdate was after enddate");
            throw ExceptionUtil.getIllegalArgumentException("exceptions.timerange.startafterend");
        }
    }
}
