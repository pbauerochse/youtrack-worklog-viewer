package de.pbauerochse.worklogviewer.connector.v2018.domain.groupby

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class GroupingFieldsDeserialisationTest extends Specification {

    def exampleFile = GroupingFieldsDeserialisationTest.class.getResource("/grouping/grouping-fields.json")

    def "replaces unknown subtypes of the grouping field with an instance of UnknownFilterField"() {
        when:
        def fields = new ObjectMapper().readValue(exampleFile, new TypeReference<List<GroupingField>>() {})

        then:
        noExceptionThrown()

        and:
        fields.findAll() { it instanceof UnknownFilterField }.size() > 0
    }
}
