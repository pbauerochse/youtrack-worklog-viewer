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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Fetches the most recent version of the
 * Worklog Viewer from Github
 */
public class VersionCheckerTask extends Task<Optional<GitHubVersion>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionCheckerTask.class);

    public VersionCheckerTask() {
        updateTitle("VersionChecker-Task");
    }

    @Override
    protected Optional<GitHubVersion> call() throws Exception {

        updateMessage(FormattingUtil.getFormatted("worker.updatecheck.checking"));
        updateProgress(0, 1);

        Optional<GitHubVersion> version = getVersionFromGithub();

        updateMessage(FormattingUtil.getFormatted("worker.progress.done"));
        updateProgress(1, 1);

        return version;
    }

    private Optional<GitHubVersion> getVersionFromGithub() throws Exception {
        HttpGet request = new HttpGet("https://api.github.com/repos/pbauerochse/youtrack-worklog-viewer/releases");
        request.addHeader("Accept", "application/json, text/plain, */*");

        try (CloseableHttpClient client = getHttpClient()) {

            try (CloseableHttpResponse response = client.execute(request)) {
                if (HttpClientUtil.isValidResponseCode(response.getStatusLine())) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());

                    List<GitHubVersion> versions = JacksonUtil.parseValue(new StringReader(jsonResponse), new TypeReference<List<GitHubVersion>>() {});

                    return Optional.ofNullable(versions)
                            .orElse(Collections.emptyList()).stream()
                            .filter(((Predicate<GitHubVersion>) GitHubVersion::isDraft).negate())
                            .max(Comparator.comparing(GitHubVersion::getPublished));
                }

                LOGGER.warn("Could not get most recent version: {}", response.getStatusLine().getReasonPhrase());
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }

        return Optional.empty();
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClientUtil.getDefaultClientBuilder(1)
                .setDefaultHeaders(HttpClientUtil.getRegularBrowserHeaders())
                .build();
    }
}
