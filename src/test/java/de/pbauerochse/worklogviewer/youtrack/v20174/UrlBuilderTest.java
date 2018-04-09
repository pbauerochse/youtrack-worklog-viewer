package de.pbauerochse.worklogviewer.youtrack.v20174;

import com.google.common.collect.ImmutableList;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class UrlBuilderTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<UrlBuilderTestdata> getTestBaseUrls() {
        return ImmutableList.of(
                new UrlBuilderTestdata("http://mybase.url.com/youtrack/", YouTrackVersion.POST_2017),
                new UrlBuilderTestdata("http://mybase.url.com/youtrack/", YouTrackVersion.POST_2018),
                new UrlBuilderTestdata("https://mybase.url.com/youtrack/", YouTrackVersion.POST_2017),
                new UrlBuilderTestdata("https://mybase.url.com/youtrack/", YouTrackVersion.POST_2018),
                new UrlBuilderTestdata("http://mybase.url.com/youtrack", YouTrackVersion.POST_2017),
                new UrlBuilderTestdata("http://mybase.url.com/youtrack", YouTrackVersion.POST_2018),
                new UrlBuilderTestdata("https://mybase.url.com/youtrack", YouTrackVersion.POST_2017),
                new UrlBuilderTestdata("https://mybase.url.com/youtrack", YouTrackVersion.POST_2018)
        );
    }

    private final UrlBuilderTestdata testdata;

    public UrlBuilderTest(UrlBuilderTestdata testdata) {
        this.testdata = testdata;
    }

    @Test
    public void doesNotContainAnyExtraSlashes() {
        UrlBuilder urlBuilder = new UrlBuilder(testdata::getBaseurl, testdata::getYoutrackVersion);

        // expect
        assertThat(urlBuilder.getCreateReportUrl(), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getDeleteReportUrl("reportid"), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getDownloadReportUrl("reportid"), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getGroupByCriteriaUrl(), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getReportDetailsUrl("reportid"), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getIssueDetailsUrl(Collections.emptyList()), pathDoesNotContainExtraSlashes());
    }

    @Test
    public void testDownloadUrlForVersion2017() {
        UrlBuilder urlBuilder = new UrlBuilder(testdata::getBaseurl, testdata::getYoutrackVersion);
        if (testdata.youtrackVersion == YouTrackVersion.POST_2017) {
            assertThat(urlBuilder.getDownloadReportUrl("_TEST_REPORT_ID_"), endsWith("/api/reports/_TEST_REPORT_ID_/export"));
        }
    }

    @Test
    public void testDownloadUrlForVersion2018() {
        UrlBuilder urlBuilder = new UrlBuilder(testdata::getBaseurl, testdata::getYoutrackVersion);
        if (testdata.youtrackVersion == YouTrackVersion.POST_2018) {
            assertThat(urlBuilder.getDownloadReportUrl("_TEST_REPORT_ID_"), endsWith("/api/reports/_TEST_REPORT_ID_/export/csv"));
        }
    }

    private Matcher<String> pathDoesNotContainExtraSlashes() {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object item) {
                String urlAsString = (String) item;
                urlAsString = StringUtils.removePattern(urlAsString, "http(s?)://");
                return !StringUtils.contains(urlAsString, "//");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Should not contain any extra slashes");
            }
        };
    }

    private static class UrlBuilderTestdata {

        private final String baseurl;
        private final YouTrackVersion youtrackVersion;

        private UrlBuilderTestdata(String baseurl, YouTrackVersion youtrackVersion) {
            this.baseurl = baseurl;
            this.youtrackVersion = youtrackVersion;
        }

        String getBaseurl() {
            return baseurl;
        }

        YouTrackVersion getYoutrackVersion() {
            return youtrackVersion;
        }

        @Override
        public String toString() {
            return String.format("Base-URL: %s, YT Version: %s", baseurl, youtrackVersion.name());
        }
    }

}