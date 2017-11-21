package de.pbauerochse.worklogviewer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.pre2017.Pre2017ReportDetailsResponse;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class JacksonSerializerTest {

    private static final String EXPECTED_SERIALIZATION_RESULT = "{\"name\":\"Timetracker: THIS_WEEK\",\"type\":\"time\",\"own\":true,\"parameters\":{\"projects\":[],\"queryUrl\":null,\"range\":{\"type\":\"named\",\"name\":\"THIS_WEEK\"}}}";
    private static final String DESERIALIZATION_TEST_DATA = "{\"id\":\"116-892\",\"name\":\"Timetracker: THIS_WEEK\",\"ownerLogin\":\"bauerochse\",\"type\":\"time\",\"own\":true,\"visibleTo\":null,\"invalidationInterval\":0,\"state\":\"CALCULATING\",\"lastCalculated\":\"—\",\"progress\":-1,\"parameters\":{\"projects\":[],\"queryUrl\":\"/issues\",\"range\":{\"type\":\"named\",\"name\":\"THIS_WEEK\"},\"perUserAvailable\":true,\"showTypesAvailable\":true}}";
    private static final String GROUP_BY_DESERIALIZATION_TEST_DATA = "[{\"name\":\"Work author\",\"id\":\"WORK_AUTHOR\"},{\"name\":\"Work type\",\"id\":\"WORK_TYPE\"},{\"name\":\"Priorität\",\"id\":\"__CUSTOM_FIELD__Priority_1\"},{\"name\":\"Typ\",\"id\":\"__CUSTOM_FIELD__Type_0\"},{\"name\":\"Status\",\"id\":\"__CUSTOM_FIELD__State_2\"},{\"name\":\"Bearbeiter\",\"id\":\"__CUSTOM_FIELD__Assignee_5\"},{\"name\":\"Komponente\",\"id\":\"__CUSTOM_FIELD__components_9\"},{\"name\":\"Lösungsversion\",\"id\":\"__CUSTOM_FIELD__Fix versions_7\"},{\"name\":\"Sprint\",\"id\":\"__CUSTOM_FIELD__Sprint_21\"},{\"name\":\"Zeitschätzung\",\"id\":\"__CUSTOM_FIELD__Estimation_12\"},{\"name\":\"Zeitaufwand\",\"id\":\"__CUSTOM_FIELD__Spent time_17\"},{\"name\":\"Abrechnung\",\"id\":\"__CUSTOM_FIELD__Abrechnung_19\"},{\"name\":\"Abnahme\",\"id\":\"__CUSTOM_FIELD__Abnahme_20\"},{\"name\":\"Verlag\",\"id\":\"__CUSTOM_FIELD__Verlag_18\"},{\"name\":\"bis wann\",\"id\":\"__CUSTOM_FIELD__bis wann_23\"},{\"name\":\"Behoben in Build\",\"id\":\"__CUSTOM_FIELD__Fixed in build_4\"}]";

    @Test
    public void testDeserialization() throws IOException {
        Pre2017ReportDetailsResponse response = JacksonUtil.parseValue(new StringReader(DESERIALIZATION_TEST_DATA), Pre2017ReportDetailsResponse.class);

        Assert.assertEquals("id", "116-892", response.getId());
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

    @Test
    public void testGroupByCategories() throws IOException {
        List<GroupByCategory> groupByCategories = JacksonUtil.parseValue(new StringReader(GROUP_BY_DESERIALIZATION_TEST_DATA), new TypeReference<List<GroupByCategory>>() {});
        Assert.assertNotNull(groupByCategories);
        Assert.assertThat(groupByCategories.size(), Is.is(16));
    }

}
