package de.pbauerochse.worklogviewer.settings.loaders

import de.pbauerochse.worklogviewer.settings.Settings
import de.pbauerochse.worklogviewer.toURL
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.isEmptyOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import java.io.StringWriter

internal class JsonSettingsLoaderTest {

    @Test
    fun `write successfully writes the settings as json object`() {
        // given
        val jsonFile = createTempFile("ytwlv", "settings.json").apply { createNewFile() }
        val loader = JsonSettingsLoader(jsonFile)

        // and
        val writer = StringWriter()

        // and
        val settings = Settings().apply {
            youTrackConnectionSettings.baseUrl = "http://localhost".toURL()
        }

        // when
        loader.write(settings, writer)

        // then
        assertThat("JsonSettingsLoader did not write proper settings file", writer.toString(), not(isEmptyOrNullString()))
    }

}
