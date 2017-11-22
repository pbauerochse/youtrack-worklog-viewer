package de.pbauerochse.worklogviewer.youtrack;

import com.google.common.collect.ImmutableSet;
import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.v20173.YouTrackServiceV20173;
import de.pbauerochse.worklogviewer.youtrack.v20174.YouTrackServiceV20174;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static de.pbauerochse.worklogviewer.util.ExceptionUtil.getIllegalArgumentException;

/**
 * Factory to get the YouTrackService
 * configured in the settings properties
 */
public class YouTrackServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTrackServiceFactory.class);

    private static final Set<YouTrackService> AVAILABLE_SERVICE_IMPLEMENTATIONS = ImmutableSet.of(
            new YouTrackServiceV20173(),
            new YouTrackServiceV20174()
    );

    private static YouTrackService cachedInstance = null;

    public static YouTrackService getInstance() {

        Settings settings = SettingsUtil.getSettings();

        if (authenticationMethodChanged(settings)) {
            cachedInstance = getYouTrackService(settings.getYouTrackVersion());

            LOGGER.info("Created new YouTrackService instance of type {}", cachedInstance.getClass().getSimpleName());
        }

        return cachedInstance;
    }

    public static YouTrackService getYouTrackService(YouTrackVersion version) {
        return AVAILABLE_SERVICE_IMPLEMENTATIONS.stream()
                .filter(connector -> connector.getVersion() == version)
                .findFirst()
                .orElseThrow(() -> getIllegalArgumentException("exceptions.settings.version.invalid", version.name()));
    }

    private static boolean authenticationMethodChanged(Settings settings) {
        return cachedInstance == null || !cachedInstance.getVersion().equals(settings.getYouTrackVersion());
    }

}
