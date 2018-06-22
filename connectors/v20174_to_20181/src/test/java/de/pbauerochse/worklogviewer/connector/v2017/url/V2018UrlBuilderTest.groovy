package de.pbauerochse.worklogviewer.connector.v2017.url

import spock.lang.Specification
import spock.lang.Subject

class V2018UrlBuilderTest extends Specification {

    @Subject
    def builder = new V2018UrlBuilder(new URL("https://youtrack.localhost/yt/"))

    def "Validate getGroupByParametersUrl"() {
        expect:
        builder.getGroupByParametersUrl().toExternalForm() == "https://youtrack.localhost/yt/api/filterFields?fieldTypes=version%5B1%5D&fieldTypes=ownedField%5B1%5D&fieldTypes=state%5B1%5D&fieldTypes=user%5B1%5D&fieldTypes=enum%5B1%5D&fieldTypes=date&fieldTypes=integer&fieldTypes=float&fieldTypes=period&fieldTypes=project&fields=id,\$type,presentation,name,aggregateable,sortable,customField(id,fieldType(id),name,localizedName),projects(id,name)&includeNonFilterFields=true"
    }

    def "Validate getCreateReportUrl"() {
        expect:
        builder.getCreateReportUrl().toExternalForm() == "https://youtrack.localhost/yt/api/reports?fields=\$type,authors(fullName,id,login,ringId),effectiveQuery,effectiveQueryUrl,id,invalidationInterval,name,own,owner(id,login),pinned,projects(id,name,shortName),query,sprint(id,name),status(calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage),visibleTo(id,name),wikifiedError,windowSize,workTypes(id,name)"
    }

    def "Validate getReportDetailsUrl"() {
        expect:
        builder.getReportDetailsUrl("_TEST_REPORT_ID_").toExternalForm() == "https://youtrack.localhost/yt/api/reports/_TEST_REPORT_ID_/status?fields=calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage"
    }

    def "Validate getDownloadReportCsvUrl"() {
        expect:
        builder.getDownloadReportCsvUrl("_TEST_REPORT_ID_").toExternalForm() == "https://youtrack.localhost/yt/api/reports/_TEST_REPORT_ID_/export/csv"
    }

    def "Validate getDeleteReportUrl"() {
        expect:
        builder.getDeleteReportUrl("_TEST_REPORT_ID_").toExternalForm() == "https://youtrack.localhost/yt/api/reports/_TEST_REPORT_ID_?fields=\$type,authors(fullName,id,login,ringId),effectiveQuery,effectiveQueryUrl,id,invalidationInterval,name,own,owner(id,login),pinned,projects(id,name,shortName),query,sprint(id,name),status(calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage),visibleTo(id,name),wikifiedError,windowSize,workTypes(id,name)"
    }

}
