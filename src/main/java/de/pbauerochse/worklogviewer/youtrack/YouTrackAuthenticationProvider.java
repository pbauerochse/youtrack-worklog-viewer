package de.pbauerochse.worklogviewer.youtrack;

import org.apache.http.Header;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.List;

public interface YouTrackAuthenticationProvider {

    List<Header> getAuthenticationHeaders(HttpClientBuilder clientBuilder, YouTrackUrlBuilder urlBuilder);

    YouTrackAuthenticationMethod getMethod();

}
