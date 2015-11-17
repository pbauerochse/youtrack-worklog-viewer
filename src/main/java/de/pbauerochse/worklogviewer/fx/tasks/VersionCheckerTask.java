package de.pbauerochse.worklogviewer.fx.tasks;

import com.fasterxml.jackson.core.type.TypeReference;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.util.HttpClientUtil;
import de.pbauerochse.worklogviewer.util.JacksonUtil;
import de.pbauerochse.worklogviewer.version.GitHubVersion;
import javafx.concurrent.Task;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;

/**
 * Created by patrick on 01.11.15.
 */
public class VersionCheckerTask extends Task<GitHubVersion> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionCheckerTask.class);

    public VersionCheckerTask() {
        updateTitle("VersionChecker-Task");
    }

    @Override
    protected GitHubVersion call() throws Exception {

        updateMessage(FormattingUtil.getFormatted("worker.updatecheck.checking"));
        updateProgress(0, 1);

        HttpGet request = new HttpGet("https://api.github.com/repos/pbauerochse/youtrack-worklog-viewer/releases");
        request.addHeader("Accept", "application/json, text/plain, */*");

        CloseableHttpClient client = HttpClientUtil.getDefaultClientBuilder(1)
                .setDefaultHeaders(HttpClientUtil.getRegularBrowserHeaders())
                .build();

        GitHubVersion version = null;

        try (CloseableHttpResponse httpResponse = client.execute(request)) {

            if (!HttpClientUtil.isValidResponseCode(httpResponse.getStatusLine())) {
                LOGGER.warn("Could not get most recent version: {}", httpResponse.getStatusLine().getReasonPhrase());
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            } else {
                String jsonResponse = EntityUtils.toString(httpResponse.getEntity());

                StringReader response = new StringReader(jsonResponse);
                List<GitHubVersion> versions = JacksonUtil.parseValue(response, new TypeReference<List<GitHubVersion>>() {
                });

                if (versions != null && versions.size() > 0) {
                    Optional<GitHubVersion> mostRecent = versions.stream()
                            .filter(gitHubVersion -> !gitHubVersion.isDraft())
                            .sorted((o1, o2) -> o2.getPublished().compareTo(o1.getPublished()))
                            .findFirst();

                    if (mostRecent.isPresent()) {
                        version = mostRecent.get();
                    }
                }
            }
        }

        updateMessage(FormattingUtil.getFormatted("worker.progress.done"));
        updateProgress(1, 1);

        return version;
    }
}
