package de.pbauerochse.worklogviewer.youtrack.connector;

import org.apache.commons.lang3.NotImplementedException;

/**
 * @author Patrick Bauerochse
 * @since 14.10.15
 */
class OAuth2Connector extends ApiLoginConnector implements YouTrackConnector {

    @Override
    protected void validateLoggedInState() throws Exception {
        throw new NotImplementedException("NOP");
    }

    @Override
    public YouTrackAuthenticationMethod getAuthenticationMethod() {
        return YouTrackAuthenticationMethod.OAUTH2;
    }
}
