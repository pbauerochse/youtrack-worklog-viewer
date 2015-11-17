package de.pbauerochse.worklogviewer.util;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicHeader;

import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrick on 01.11.15.
 */
public class HttpClientUtil {

    public static HttpClientBuilder getDefaultClientBuilder(int connectTimeoutInSeconds) {
        RequestConfig config = RequestConfig
                .custom()
                .setConnectTimeout(connectTimeoutInSeconds * 1000)
                .setConnectionRequestTimeout(connectTimeoutInSeconds * 1000)
                .build();

        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());

        return HttpClients
                .custom()
                .setDefaultRequestConfig(config)
                .setRoutePlanner(routePlanner);
    }

    public static List<Header> getRegularBrowserHeaders() {
        List<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36"));
        headerList.add(new BasicHeader("Accept-Encoding", "gzip, deflate, sdch"));
        headerList.add(new BasicHeader("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4"));
        return headerList;
    }

    public static boolean isValidResponseCode(StatusLine statusLine) {
        if (statusLine == null) throw ExceptionUtil.getIllegalArgumentException("exceptions.main.worker.nullstatus");
        int statusCode = statusLine.getStatusCode();

        return (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES);
    }

}
