package de.pbauerochse.worklogviewer.connector.v2017.url

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URL


internal class V2018UrlBuilderTest {

    private val builder = V2018UrlBuilder(URL("https://youtrack.localhost/yt/"))

    @Test
    fun `Validate getGroupByParametersUrl`() = assertEquals(
        "https://youtrack.localhost/yt/api/filterFields?fieldTypes=version%5B1%5D&fieldTypes=ownedField%5B1%5D&fieldTypes=state%5B1%5D&fieldTypes=user%5B1%5D&fieldTypes=enum%5B1%5D&fieldTypes=date&fieldTypes=integer&fieldTypes=float&fieldTypes=period&fieldTypes=project&fields=id,\$type,presentation,name,aggregateable,sortable,customField(id,fieldType(id),name,localizedName),projects(id,name)&includeNonFilterFields=true",
        builder.getGroupByParametersUrl().toExternalForm()
    )

    @Test
    fun `Validate getCreateReportUrl`() = assertEquals(
        "https://youtrack.localhost/yt/api/reports?fields=\$type,authors(fullName,id,login,ringId),effectiveQuery,effectiveQueryUrl,id,invalidationInterval,name,own,owner(id,login),pinned,projects(id,name,shortName),query,sprint(id,name),status(calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage),visibleTo(id,name),wikifiedError,windowSize,workTypes(id,name)",
        builder.getCreateReportUrl().toExternalForm()
    )

    @Test
    fun `Validate getReportDetailsUrl`() = assertEquals(
        "https://youtrack.localhost/yt/api/reports/_TEST_REPORT_ID_/status?fields=calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage",
        builder.getReportDetailsUrl("_TEST_REPORT_ID_").toExternalForm()
    )

    @Test
    fun `Validate getDownloadReportCsvUrl`() = assertEquals(
        "https://youtrack.localhost/yt/api/reports/_TEST_REPORT_ID_/export/csv",
        builder.getDownloadReportCsvUrl("_TEST_REPORT_ID_").toExternalForm()
    )

    @Test
    fun `Validate getDeleteReportUrl`() = assertEquals(
        "https://youtrack.localhost/yt/api/reports/_TEST_REPORT_ID_?fields=\$type,authors(fullName,id,login,ringId),effectiveQuery,effectiveQueryUrl,id,invalidationInterval,name,own,owner(id,login),pinned,projects(id,name,shortName),query,sprint(id,name),status(calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage),visibleTo(id,name),wikifiedError,windowSize,workTypes(id,name)",
        builder.getDeleteReportUrl("_TEST_REPORT_ID_").toExternalForm()
    )

}
