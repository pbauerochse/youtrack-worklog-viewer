package de.pbauerochse.worklogviewer.youtrack.authentication;

import com.google.common.collect.ImmutableSet;
import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationProvider;

import java.util.Set;

public class YouTrackAuthenticationProviderFactory {

    private static final Set<YouTrackAuthenticationProvider> PROVIDERS = ImmutableSet.of(
            new ApiLoginAuthenticationProvider(),
            new OAuth2AuthenticationProvider(),
            new BearerTokenAuthenticationProvider()
    );

    public static YouTrackAuthenticationProvider getActiveProvider() {
        Settings settings = SettingsUtil.getSettings();
        YouTrackAuthenticationMethod authenticationMethod = settings.getYouTrackAuthenticationMethod();

        return PROVIDERS.stream()
                .filter(provider -> provider.getMethod() == authenticationMethod)
                .findFirst()
                .orElseThrow(() -> ExceptionUtil.getIllegalStateException("exceptions.settings.authentication.invalid", authenticationMethod.name()));
    }

}
