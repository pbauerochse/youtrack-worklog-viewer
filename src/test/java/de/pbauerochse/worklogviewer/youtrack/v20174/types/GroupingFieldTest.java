package de.pbauerochse.worklogviewer.youtrack.v20174.types;

import com.fasterxml.jackson.core.type.TypeReference;
import de.pbauerochse.worklogviewer.util.JacksonUtil;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class GroupingFieldTest {

    @Test
    public void shouldProperlyDeserializeGroupByFieldsResponse() throws Exception {
        InputStream json = GroupingFieldTest.class.getResourceAsStream("/json/v2017.4/grouping-fields.json");
        List<GroupingField> fields = JacksonUtil.parseValue(new InputStreamReader(json), new TypeReference<List<GroupingField>>() {
        });

        assertThat(fields, hasSize(9));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void containsExpectedInstances() throws Exception {
        InputStream json = GroupingFieldTest.class.getResourceAsStream("/json/v2017.4/grouping-fields.json");
        List<GroupingField> fields = JacksonUtil.parseValue(new InputStreamReader(json), new TypeReference<List<GroupingField>>() {
        });

        assertThat(fields, hasItems(
                instanceOf(GroupByTypes.class),
                instanceOf(GroupByTypes.class),
                instanceOf(GroupByTypes.class),
                instanceOf(PredefinedFilterField.class),
                instanceOf(CustomFilterField.class),
                instanceOf(CustomFilterField.class),
                instanceOf(CustomFilterField.class),
                instanceOf(CustomFilterField.class),
                instanceOf(CustomFilterField.class)
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void hasProperIds() throws Exception {
        InputStream json = GroupingFieldTest.class.getResourceAsStream("/json/v2017.4/grouping-fields.json");
        List<GroupingField> fields = JacksonUtil.parseValue(new InputStreamReader(json), new TypeReference<List<GroupingField>>() {
        });

        assertThat(fields, hasItems(
                hasProperty("id", is("WORK_TYPE")),
                hasProperty("id", is("WORK_AUTHOR")),
                hasProperty("id", is("WORK_AUTHOR_AND_DATE")),
                hasProperty("id", is("Projekt")),
                hasProperty("id", is("50-4")),
                hasProperty("id", is("50-0")),
                hasProperty("id", is("50-1")),
                hasProperty("id", is("50-3")),
                hasProperty("id", is("50-2"))
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void hasProperPresentation() throws Exception {
        InputStream json = GroupingFieldTest.class.getResourceAsStream("/json/v2017.4/grouping-fields.json");
        List<GroupingField> fields = JacksonUtil.parseValue(new InputStreamReader(json), new TypeReference<List<GroupingField>>() {
        });

        assertThat(fields, hasItems(
                hasProperty("presentation", is("Arbeitstyp")),
                hasProperty("presentation", is("Autor der Arbeit")),
                hasProperty("presentation", is("Autor der Arbeit und Datum")),
                hasProperty("presentation", is("Projekt")),
                hasProperty("presentation", is("Bearbeiter")),
                hasProperty("presentation", is("Teilsystem")),
                hasProperty("presentation", is("Priorität")),
                hasProperty("presentation", is("Status")),
                hasProperty("presentation", is("Typ"))
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void predefinedFilterFieldContainsAllExpectedAttributes() throws Exception {
        InputStream json = GroupingFieldTest.class.getResourceAsStream("/json/v2017.4/grouping-fields.json");
        List<GroupingField> fields = JacksonUtil.parseValue(new InputStreamReader(json), new TypeReference<List<GroupingField>>() {
        });

        PredefinedFilterField predefinedFilterField = (PredefinedFilterField) fields.get(3);

        assertFalse(predefinedFilterField.isAggregateable());
        assertTrue(predefinedFilterField.isSortable());
        assertThat(predefinedFilterField.getName(), is("Projekt"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void customFilterFieldsContainCustomFieldAttribute() throws Exception {
        InputStream json = GroupingFieldTest.class.getResourceAsStream("/json/v2017.4/grouping-fields.json");
        List<GroupingField> fields = JacksonUtil.parseValue(new InputStreamReader(json), new TypeReference<List<GroupingField>>() {
        });

        assertThat(fields.get(4), hasProperty("customField", notNullValue()));
        assertThat(fields.get(5), hasProperty("customField", notNullValue()));
        assertThat(fields.get(6), hasProperty("customField", notNullValue()));
        assertThat(fields.get(7), hasProperty("customField", notNullValue()));
        assertThat(fields.get(8), hasProperty("customField", notNullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void customFilterFieldsContainProjectsAttribute() throws Exception {
        InputStream json = GroupingFieldTest.class.getResourceAsStream("/json/v2017.4/grouping-fields.json");
        List<GroupingField> fields = JacksonUtil.parseValue(new InputStreamReader(json), new TypeReference<List<GroupingField>>() {
        });

        assertThat(fields.get(4), hasProperty("projects", hasSize(2)));
        assertThat(fields.get(5), hasProperty("projects", hasSize(2)));
        assertThat(fields.get(6), hasProperty("projects", hasSize(2)));
        assertThat(fields.get(7), hasProperty("projects", hasSize(2)));
        assertThat(fields.get(8), hasProperty("projects", hasSize(2)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void projectsListContainsValidProjects() throws Exception {
        InputStream json = GroupingFieldTest.class.getResourceAsStream("/json/v2017.4/grouping-fields.json");
        List<GroupingField> fields = JacksonUtil.parseValue(new InputStreamReader(json), new TypeReference<List<GroupingField>>() {
        });

        Matcher<Iterable<Project>> projectListMatcher = hasItems(
                allOf(
                        hasProperty("id", is("77-0")),
                        hasProperty("name", is("Test-Projekt"))
                ),

                allOf(
                        hasProperty("id", is("77-1")),
                        hasProperty("name", is("Weiteres Projekt"))
                )
        );

        assertThat(fields.get(4), hasProperty("projects", projectListMatcher));
        assertThat(fields.get(5), hasProperty("projects", projectListMatcher));
        assertThat(fields.get(6), hasProperty("projects", projectListMatcher));
        assertThat(fields.get(7), hasProperty("projects", projectListMatcher));
        assertThat(fields.get(8), hasProperty("projects", projectListMatcher));
    }
}