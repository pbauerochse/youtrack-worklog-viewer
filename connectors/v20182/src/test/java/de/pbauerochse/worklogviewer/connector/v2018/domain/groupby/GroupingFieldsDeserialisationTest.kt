package de.pbauerochse.worklogviewer.connector.v2018.domain.groupby

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class GroupingFieldsDeserialisationTest {

    private val exampleFile = GroupingFieldsDeserialisationTest::class.java.getResource("/")

    @Test
    internal fun `replaces unknown subtypes of the grouping field with an instance of UnknownFilterField`() {
        // when
        val fields = ObjectMapper().readValue<List<GroupingField>>(exampleFile, object : TypeReference<List<GroupingField>>() {})

        // then
        assertTrue(fields.none { it is UnknownFilterField }) { "There should not have been an UnknownFilterField" }
    }

}
