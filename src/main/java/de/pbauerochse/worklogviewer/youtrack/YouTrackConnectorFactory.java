package de.pbauerochse.worklogviewer.youtrack;

import com.google.common.collect.ImmutableSet;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.connector.ApiLoginConnector;
import de.pbauerochse.worklogviewer.youtrack.connector.BearerTokenConnector;
import de.pbauerochse.worklogviewer.youtrack.connector.OAuth2Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static de.pbauerochse.worklogviewer.util.ExceptionUtil.getIllegalArgumentException;

/**
 * Factory to get the YouTrackConnector
 * configured in the settings properties
 */
public class YouTrackConnectorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTrackConnectorFactory.class);

    private static final YouTrackConnector DEFAULT_CONNECTOR = new ApiLoginConnector();
    private static final Set<YouTrackConnector> AVAILABLE_CONNECTORS = ImmutableSet.of(
            DEFAULT_CONNECTOR,
            new OAuth2Connector(),
            new BearerTokenConnector()
    );

    private static YouTrackConnector cachedInstance = DEFAULT_CONNECTOR;

    public static YouTrackConnector getInstance() {

        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        if (authenticationMethodChanged(settings)) {
            cachedInstance = AVAILABLE_CONNECTORS.stream()
                    .filter(connector -> connector.getAuthenticationMethod() == settings.getYouTrackAuthenticationMethod())
                    .findFirst()
                    .orElseThrow(() -> getIllegalArgumentException("exceptions.settings.authentication.invalid", settings.getYouTrackAuthenticationMethod().name()));

            LOGGER.info("Created new connector instance of type {}", cachedInstance.getClass().getSimpleName());
        }

        return cachedInstance;
    }

    private static boolean authenticationMethodChanged(SettingsUtil.Settings settings) {
        return cachedInstance == null || !cachedInstance.getAuthenticationMethod().equals(settings.getYouTrackAuthenticationMethod());
    }

}
