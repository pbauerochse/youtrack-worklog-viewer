package de.pbauerochse.worklogviewer.connector.v2017.domain.report

import com.fasterxml.jackson.databind.ObjectMapper
import de.pbauerochse.worklogviewer.connector.v2017.Translations
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.CustomField
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.CustomFilterField
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.FieldType
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.GroupByTypes
import de.pbauerochse.worklogviewer.report.TimeRange
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class CreateReportRequestParametersTest extends Specification {

    @Unroll
    def "Serialization contains field #field"() {
        given:
        def reportParams = Mock(TimeReportParameters)
        reportParams.timerange >> new TimeRange(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 7))
        reportParams.groupByParameter >> new GroupByTypes("WORK_TYPE", Translations.i18n.get("grouping.worktype"))

        and:
        def params = new CreateReportRequestParameters(reportParams)

        when:
        def result = new ObjectMapper().writeValueAsString(params)

        then:
        result.contains("\"$field\"")

        where:
        field << [
                "name", "\$type", "type", "own", "range", "grouping"
        ]
    }

    def "Snapshot comparison standard grouping"() {
        given:
        def snapshot = CreateReportRequestParametersTest.class
                .getResourceAsStream("/report/request/snapshot-create-report-request-with-standard-grouping.json")
                .getText("UTF-8")

        and:
        def reportParams = Mock(TimeReportParameters)
        reportParams.timerange >> new TimeRange(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 7))
        reportParams.groupByParameter >> new GroupByTypes("WORK_TYPE", Translations.i18n.get("grouping.worktype"))

        and:
        def params = new CreateReportRequestParameters(reportParams)

        expect:
        new ObjectMapper().writeValueAsString(params) == snapshot
    }

    def "Snapshot comparison field based grouping"() {
        given:
        def snapshot = CreateReportRequestParametersTest.class
                .getResourceAsStream("/report/request/snapshot-create-report-request-with-field-based-grouping.json")
                .getText("UTF-8")

        and:
        def reportParams = Mock(TimeReportParameters)
        reportParams.timerange >> new TimeRange(LocalDate.of(2018, 1, 1), LocalDate.of(2018, 1, 7))
        reportParams.groupByParameter >> new FieldBasedGrouping(new CustomFilterField(
                "40-5", "Bearbeiter",
                new CustomField("40-5", "Assignee",
                        new FieldType("user[1]"),
                        "Bearbeiter"
                ),
                [],
                "Bearbeiter",
                false, true
        ))

        and:
        def params = new CreateReportRequestParameters(reportParams)

        expect:
        new ObjectMapper().writeValueAsString(params) == snapshot
    }

}
