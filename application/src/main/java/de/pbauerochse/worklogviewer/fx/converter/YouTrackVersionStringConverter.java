package de.pbauerochse.worklogviewer.fx.converter;

import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator;
import de.pbauerochse.worklogviewer.connector.YouTrackVersion;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;


public class YouTrackVersionStringConverter extends StringConverter<YouTrackVersion> {

    @Override
    public String toString(YouTrackVersion object) {
        return object.getLabel();
    }

    @Override
    public YouTrackVersion fromString(String versionLabel) {
        return YouTrackConnectorLocator
                .getSupportedVersions().stream()
                .filter(it -> StringUtils.equals(it.getLabel(), versionLabel))
                .findAny()
                .orElse(null);
    }

}
