package de.pbauerochse.worklogviewer.connector.v2017.domain.report

import com.fasterxml.jackson.databind.ObjectMapper
import de.pbauerochse.worklogviewer.connector.v2017.Translations
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.CustomField
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.CustomFilterField
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.FieldType
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.GroupByTypes
import de.pbauerochse.worklogviewer.report.TimeRange
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.charset.StandardCharsets
import java.time.LocalDate

internal class CreateReportRequestParametersTest {

    @ParameterizedTest
    @ValueSource(strings = ["name", "\$type", "type", "own", "range", "grouping"])
    internal fun `Serialization contains expected field`(field: String) {
        // given
        val timerange = TimeRange(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 7))
        val groupByParameter = GroupByTypes("WORK_TYPE", Translations.i18n.get("grouping.worktype"))
        val reportParams = TimeReportParameters(timerange, FieldBasedGrouping(groupByParameter))

        // and
        val params = CreateReportRequestParameters(reportParams)

        // when
        val result = ObjectMapper().writeValueAsString(params)

        // then
        assertThat("Field $field could not be found in serialized content", result, containsString(field))
    }

    @Test
    internal fun `Snapshot comparison standard grouping`() {
        // given
        val snapshot = CreateReportRequestParametersTest::class.java.getResourceAsStream("/report/request/snapshot-create-report-request-with-standard-grouping.json").reader(StandardCharsets.UTF_8).use {
            it.readLines().joinToString()
        }

        // and
        val timerange = TimeRange(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 7))
        val groupByParameter = GroupByTypes("WORK_TYPE", Translations.i18n.get("grouping.worktype"))
        val reportParams = TimeReportParameters(timerange, FieldBasedGrouping(groupByParameter))

        // and
        val params = CreateReportRequestParameters(reportParams)

        // when
        val result = ObjectMapper().writeValueAsString(params)

        // then
        assertEquals(snapshot, result) { "Serialized result does not match expected snapshot" }
    }

    @Test
    internal fun `Snapshot comparison field based grouping`() {
        // given
        val snapshot = CreateReportRequestParametersTest::class.java.getResourceAsStream("/report/request/snapshot-create-report-request-with-field-based-grouping.json").reader(StandardCharsets.UTF_8).use {
            it.readLines().joinToString()
        }

        // and
        val timerange = TimeRange(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 7))
        val groupByParameter = CustomFilterField("40-5", "Bearbeiter", CustomField("40-5", "Assignee", FieldType("user[1]"), "Bearbeiter"), emptyList(), "Bearbeiter", aggregateable = false, sortable = true)
        val reportParams = TimeReportParameters(timerange, FieldBasedGrouping(groupByParameter))

        // and
        val params = CreateReportRequestParameters(reportParams)

        // when
        val result = ObjectMapper().writeValueAsString(params)

        // then
        assertEquals(snapshot, result) { "Serialized result does not match expected snapshot" }
    }

}
