package de.pbauerochse.worklogviewer.connector.v2017.url

import de.pbauerochse.worklogviewer.connector.YouTrackConnectionSettings
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.connector.v2017.SupportedVersions
import spock.lang.Specification
import spock.lang.Unroll

class UrlBuilderFactoryTest extends Specification {

    @Unroll
    def "Returns an url builder for version #version"() {
        given:
        def settings = Mock(YouTrackConnectionSettings)
        settings.version >> version
        settings.baseUrl >> new URL("http://localhost")

        expect:
        UrlBuilderFactory.getUrlBuilder(settings) != null

        where:
        version << [SupportedVersions.v2017_4, SupportedVersions.v2018_1]
    }

    def "Throws an Exception when invoked with an unsupported version"() {
        given:
        def settings = Mock(YouTrackConnectionSettings)
        settings.version >> new YouTrackVersion("UNSUPPORTED")

        when:
        UrlBuilderFactory.getUrlBuilder(settings)

        then:
        thrown IllegalArgumentException
    }

}
