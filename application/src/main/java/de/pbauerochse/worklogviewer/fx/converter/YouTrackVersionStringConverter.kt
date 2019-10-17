package de.pbauerochse.worklogviewer.fx.converter

import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator
import de.pbauerochse.worklogviewer.connector.YouTrackVersion
import javafx.util.StringConverter

object YouTrackVersionStringConverter : StringConverter<YouTrackVersion>() {

    override fun toString(youTrackVersion: YouTrackVersion?): String? {
        return youTrackVersion?.label
    }

    override fun fromString(versionLabel: String): YouTrackVersion? {
        return YouTrackConnectorLocator
            .getSupportedVersions()
            .firstOrNull { it.label == versionLabel }
    }

}
