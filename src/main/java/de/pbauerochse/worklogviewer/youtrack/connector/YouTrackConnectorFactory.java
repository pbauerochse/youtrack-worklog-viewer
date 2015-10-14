package de.pbauerochse.worklogviewer.youtrack.connector;

import de.pbauerochse.worklogviewer.util.SettingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick Bauerochse
 * @since 14.10.15
 */
public class YouTrackConnectorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTrackConnectorFactory.class);

    private static YouTrackConnector instance;

    public static YouTrackConnector getInstance() {

        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        if (instance == null || !instance.getAuthenticationMethod().equals(settings.getYouTrackAuthenticationMethod())) {
            // need to initialize new connector


            switch (settings.getYouTrackAuthenticationMethod()) {
                case OAUTH2:    instance = new OAuth2Connector();
                    break;

                case HTTP_API:
                default:        instance = new ApiLoginConnector();
                    break;
            }

            LOGGER.info("Created new connector instance of type {}", instance.getClass().getSimpleName());
        }

        return instance;
    }

}
