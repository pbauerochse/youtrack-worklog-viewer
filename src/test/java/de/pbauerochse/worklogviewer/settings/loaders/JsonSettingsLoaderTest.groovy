package de.pbauerochse.worklogviewer.settings.loaders

import de.pbauerochse.worklogviewer.settings.Settings
import spock.lang.Specification

class JsonSettingsLoaderTest extends Specification {

    def "write successfully writes the settings as json object"() {
        given:
        def loader = new JsonSettingsLoader(new File("./test.json"))

        and:
        def writer = new StringWriter()

        when:
        loader.write(new Settings(), writer)

        then:
        noExceptionThrown()
    }

}
