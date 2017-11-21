package de.pbauerochse.worklogviewer.youtrack.post2017;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.domain.timerangeprovider.TimerangeProviderFactory;
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportContext;
import de.pbauerochse.worklogviewer.util.JacksonUtil;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

/**
 * @author Patrick Brandes
 */
public class Post2017CreateReportRequestPayloadTest {

    @Test
    public void meh() {
        ReportTimerange reportTimerange = ReportTimerange.CUSTOM;
        LocalDate start = LocalDate.of(2017, Month.NOVEMBER, 1);
        LocalDate end = LocalDate.of(2017, Month.NOVEMBER, 21);
        GroupByCategory groupByCategory = new GroupByCategory("WORK_AUTHOR", "Work author");

        TimerangeProvider timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(reportTimerange, start, end);
        FetchTimereportContext context = new FetchTimereportContext(timerangeProvider, groupByCategory);
        Post2017CreateReportRequestPayload payload = new Post2017CreateReportRequestPayload(context);
        System.out.println(JacksonUtil.writeObject(payload));
    }

}