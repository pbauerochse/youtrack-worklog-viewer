package de.pbauerochse.worklogviewer.youtrack.csv

import spock.lang.Specification
import spock.lang.Unroll

class CsvReportReaderTest extends Specification {

    def exampleFile = CsvReportReaderTest.class.getResource("/report/example-response.csv").file

    def "Successfully processes example csv file"() {
        when:
        CsvReportReader.processResponse(new FileInputStream(exampleFile))

        then:
        noExceptionThrown()
    }

    def "Contains the expected amount of Projects"() {
        when:
        def result = CsvReportReader.processResponse(new FileInputStream(exampleFile))

        then:
        result.projects.size() == 23
    }

    @Unroll
    def "#project contains #issueCount issues"() {
        given:
        def result = CsvReportReader.processResponse(new FileInputStream(exampleFile))

        expect:
        result.getProject(project).issues.size() == issueCount

        where:
        project | issueCount
        "PROJECT1" | 3
        "PROJECT2" | 1
        "PROJECT3" | 19
        "PROJECT4" | 10
        "PROJECT5" | 1
        "PROJECT6" | 8
        "PROJECT7" | 5
        "PROJECT8" | 3
        "PROJECT9" | 2
        "PROJECT10" | 7
        "PROJECT11" | 3
        "PROJECT12" | 8
        "PROJECT13" | 43
        "PROJECT14" | 9
        "PROJECT15" | 1
        "PROJECT16" | 15
        "PROJECT17" | 1
        "PROJECT18" | 22
        "PROJECT19" | 7
        "PROJECT20" | 7
        "PROJECT21" | 3
        "PROJECT22" | 1
        "PROJECT23" | 7
    }

}
