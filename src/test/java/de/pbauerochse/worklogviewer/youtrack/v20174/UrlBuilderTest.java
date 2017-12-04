package de.pbauerochse.worklogviewer.youtrack.v20174;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class UrlBuilderTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<String> getTestData() {
        return ImmutableList.of(
                "http://mybase.url.com/youtrack/",
                "https://mybase.url.com/youtrack/",
                "http://mybase.url.com/youtrack",
                "https://mybase.url.com/youtrack"
        );
    }

    private final String baseurl;

    public UrlBuilderTest(String baseurl) {
        this.baseurl = baseurl;
    }

    @Test
    public void doesNotContainAnyExtraSlashes() {
        UrlBuilder urlBuilder = new UrlBuilder(() -> baseurl);

        // expect
        assertThat(urlBuilder.getCreateReportUrl(), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getDeleteReportUrl("reportid"), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getDownloadReportUrl("reportid"), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getGroupByCriteriaUrl(), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getReportDetailsUrl("reportid"), pathDoesNotContainExtraSlashes());
        assertThat(urlBuilder.getIssueDetailsUrl(Collections.emptyList()), pathDoesNotContainExtraSlashes());
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


}