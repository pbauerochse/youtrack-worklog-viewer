package de.pbauerochse.youtrack.util;

import de.pbauerochse.youtrack.connector.createreport.request.CreateReportRequestEntity;
import de.pbauerochse.youtrack.connector.createreport.response.ReportDetailsResponse;
import de.pbauerochse.youtrack.domain.ReportTimerange;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class JacksonSerializerTest {

    public static final String EXPECTED_SERIALIZATION_RESULT = "{\"name\":\"Timetracker: THIS_WEEK\",\"type\":\"time\",\"own\":true,\"parameters\":{\"projects\":[],\"queryUrl\":null,\"range\":{\"type\":\"named\",\"name\":\"THIS_WEEK\"}}}";
    public static final String DESERIALIZATION_TEST_DATA = "{\"id\":\"116-37\",\"name\":\"Timetracker: THIS_WEEK\",\"ownerLogin\":\"bauerochse\",\"type\":\"time\",\"own\":true,\"visibleTo\":null,\"invalidationInterval\":0,\"state\":\"CALCULATING\",\"lastCalculated\":\"—\",\"progress\":-1,\"parameters\":{\"projects\":[],\"queryUrl\":\"/issues\"}}";

    @Test
    public void testCreateReportParameters() throws Exception {
        CreateReportRequestEntity entity = new CreateReportRequestEntity(ReportTimerange.THIS_WEEK);
        String asString = JacksonUtil.writeObject(entity);
        Assert.assertEquals(EXPECTED_SERIALIZATION_RESULT, asString);
    }

    @Test
    public void testDeserialization() throws IOException {
        ReportDetailsResponse response = JacksonUtil.parseValue(new StringReader(DESERIALIZATION_TEST_DATA), ReportDetailsResponse.class);

        Assert.assertEquals("id", "116-37", response.getId());
        Assert.assertEquals("name", "Timetracker: THIS_WEEK", response.getName());
        Assert.assertEquals("ownerLogin", "bauerochse", response.getOwnerLogin());
        Assert.assertEquals("type", "time", response.getType());
        Assert.assertEquals("own", true, response.isOwn());
        Assert.assertEquals("invalidationInterval", 0, (long) response.getInvalidationInterval());
        Assert.assertEquals("state", "CALCULATING", response.getState());
        Assert.assertEquals("lastCalculated", "—", response.getLastCalculated());
        Assert.assertEquals("progress", -1, (long) response.getProgress());
        Assert.assertNotNull("parameters", response.getParameters());
        Assert.assertNotNull("parameters.projects", response.getParameters().getProjects());
        Assert.assertEquals("parameters.projects.size", 0, response.getParameters().getProjects().size());
        Assert.assertEquals("parameters.queryUrl", "/issues", response.getParameters().getQueryUrl());
    }

}
