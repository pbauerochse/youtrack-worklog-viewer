package de.pbauerochse.worklogviewer.datasource

import de.pbauerochse.worklogviewer.datasource.api.RestApiDataSourceFactory
import de.pbauerochse.worklogviewer.datasource.dummy.DummyDataSourceFactory
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class DataSourcesTest {

    @Test
    fun `REST API Connector is available within the application`() {
        assertNotNull(DataSources.findDataSourceFactoryById(RestApiDataSourceFactory.CONNECTOR_ID))
    }

    @Test
    fun `Dummy Connector is available within the application`() {
        assertNotNull(DataSources.findDataSourceFactoryById(DummyDataSourceFactory.CONNECTOR_ID))
    }

}