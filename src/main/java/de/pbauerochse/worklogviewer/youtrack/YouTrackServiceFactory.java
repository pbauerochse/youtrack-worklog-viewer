package de.pbauerochse.worklogviewer.youtrack;

import com.google.common.collect.ImmutableSet;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.post2017.Post2017YouTrackService;
import de.pbauerochse.worklogviewer.youtrack.pre2017.Pre2017YouTrackService;
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
            new Pre2017YouTrackService(),
            new Post2017YouTrackService()
    );

    private static YouTrackService cachedInstance = null;

    public static YouTrackService getInstance() {

        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

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

    private static boolean authenticationMethodChanged(SettingsUtil.Settings settings) {
        return cachedInstance == null || !cachedInstance.getVersion().equals(settings.getYouTrackVersion());
    }

}
