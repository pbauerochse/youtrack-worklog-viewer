package de.pbauerochse.worklogviewer.connector.v2017.url

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.connector.v2017.SupportedVersions
import de.pbauerochse.worklogviewer.version.Version
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.net.URL

internal class UrlBuilderFactoryTest {

    @ParameterizedTest
    @MethodSource("versions")
    internal fun `Returns an url builder for each supported version`(version: YouTrackVersion) {
        // given
        val settings = mock(YouTrackConnectionSettings::class.java)
        `when`(settings.version).thenReturn(version)
        `when`(settings.baseUrl).thenReturn(URL("http://localhost"))

        // when
        val builder = UrlBuilderFactory.getUrlBuilder(settings)

        // then
        assertNotNull(builder)
    }

    @Test
    internal fun `Throws an Exception when invoked with an unsupported version`() {
        // given
        val settings = mock(YouTrackConnectionSettings::class.java)
        `when`(settings.version).thenReturn(YouTrackVersion("UNSUPPORTED", "UNSUPPORTED", Version(0, 0, 0)))

        // expect
        assertThrows(IllegalArgumentException::class.java) { UrlBuilderFactory.getUrlBuilder(settings) }
    }

    companion object {
        @JvmStatic
        fun versions(): List<YouTrackVersion> = listOf(SupportedVersions.v2017_4, SupportedVersions.v2018_1)
    }

}
