package de.pbauerochse.worklogviewer.youtrack.authentication;

import com.google.common.collect.ImmutableList;
import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.HttpClientUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationProvider;
import de.pbauerochse.worklogviewer.youtrack.YouTrackUrlBuilder;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 14.10.15
 */
public class ApiLoginAuthenticationProvider implements YouTrackAuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLoginAuthenticationProvider.class);

    private Integer connectionParameterHashCode;
    private CookieStore cookieStore = new BasicCookieStore();

    @Override
    public List<Header> getAuthenticationHeaders(HttpClientBuilder clientBuilder, YouTrackUrlBuilder urlBuilder) {

        if (isReauthenticationRequired()) {
            String url = urlBuilder.getUsernamePasswordLoginUrl();
            LOGGER.debug("Logging in using url {}", url);

            try (CloseableHttpClient httpClient = getLoginHttpClient(cookieStore)) {
                HttpPost request = new HttpPost(url);
                request.setEntity(new UrlEncodedFormEntity(getCredentials(), StandardCharsets.UTF_8));

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    HttpEntity entity = response.getEntity();
                    EntityUtils.consumeQuietly(entity);

                    if (!HttpClientUtil.isValidResponseCode(response.getStatusLine())) {
                        throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.login", response.getStatusLine().getReasonPhrase(), response.getStatusLine().getStatusCode());
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Login to YouTrack failed", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.unknown");
            }
        }

        // at this point, the cookieStore should contain
        // a valid login cookie, which can be used for all
        // subsequent requests
        clientBuilder.setDefaultCookieStore(cookieStore);

        return Collections.emptyList();
    }

    private CloseableHttpClient getLoginHttpClient(CookieStore cookieStore) {
        return HttpClientUtil.getDefaultClientBuilder(10)
                .setDefaultHeaders(HttpClientUtil.getRegularBrowserHeaders())
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    private List<NameValuePair> getCredentials() {
        Settings settings = SettingsUtil.getSettings();
        return ImmutableList.of(
                new BasicNameValuePair("login", settings.getYoutrackUsername()),
                new BasicNameValuePair("password", settings.getYoutrackPassword())
        );
    }

    @Override
    public YouTrackAuthenticationMethod getMethod() {
        return YouTrackAuthenticationMethod.HTTP_API;
    }

    private boolean isReauthenticationRequired() {
        return cookieStore.getCookies().isEmpty() || cookieStore.clearExpired(new Date());
    }

}
