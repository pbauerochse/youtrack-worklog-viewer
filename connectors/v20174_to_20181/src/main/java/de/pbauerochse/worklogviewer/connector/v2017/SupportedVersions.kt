package de.pbauerochse.worklogviewer.connector.v2017

import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import de.pbauerochse.worklogviewer.version.Version

/**
 * The supported versions for this
 * connector implementation
 */
object SupportedVersions {

    @JvmStatic
    val v2017_4 = YouTrackVersion("v2017.4", "2017.4 - 2018.1", Version(2017, 4, 0))

    @JvmStatic
    val v2018_1 = YouTrackVersion("v2018.1", "2018.1", Version(2018, 1, 0))

}