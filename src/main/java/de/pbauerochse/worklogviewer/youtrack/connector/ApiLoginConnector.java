package de.pbauerochse.worklogviewer.youtrack.connector;

import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.HttpClientUtil;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 14.10.15
 */
class ApiLoginConnector extends YouTrackConnectorBase {

    private Integer connectionParameterHashCode;

    private CloseableHttpClient loggedInClient;

    @Override
    protected CloseableHttpClient performLoginIfNecessary(HttpClientBuilder clientBuilder, List<Header> requestHeaders) throws Exception {

        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        if (loggedInClient == null || (connectionParameterHashCode != null && !connectionParameterHashCode.equals(settings.getConnectionParametersHashCode()))) {
            LOGGER.debug("New connection or connection parameters changed. Performing login");

            String loginUrl = buildYoutrackApiUrl("user/login");

            HttpPost request = new HttpPost(loginUrl);

            List<NameValuePair> requestParameters = new ArrayList<>();
            requestParameters.add(new BasicNameValuePair("login", settings.getYoutrackUsername()));
            requestParameters.add(new BasicNameValuePair("password", settings.getYoutrackPassword()));
            request.setEntity(new UrlEncodedFormEntity(requestParameters, "utf-8"));

            CloseableHttpClient loginClient = clientBuilder
                    .setDefaultHeaders(requestHeaders)
                    .build();

            CloseableHttpResponse response = loginClient.execute(request);

            try {
                EntityUtils.consumeQuietly(response.getEntity());
            } finally {
                response.close();
            }

            if (!HttpClientUtil.isValidResponseCode(response.getStatusLine())) {
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.login", response.getStatusLine().getReasonPhrase(), response.getStatusLine().getStatusCode());
            }

            // connection successfull with these settings, headers have been set
            // hence store the parameters hash and the client
            connectionParameterHashCode = settings.getConnectionParametersHashCode();
            loggedInClient = loginClient;
        }

        return loggedInClient;
    }

    @Override
    public YouTrackAuthenticationMethod getAuthenticationMethod() {
        return YouTrackAuthenticationMethod.HTTP_API;
    }
}
