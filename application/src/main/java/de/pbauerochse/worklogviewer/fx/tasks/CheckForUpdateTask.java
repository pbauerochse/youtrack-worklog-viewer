package de.pbauerochse.worklogviewer.fx.tasks;

import com.fasterxml.jackson.core.type.TypeReference;
import de.pbauerochse.worklogviewer.tasks.ProgressCallback;
import de.pbauerochse.worklogviewer.util.HttpClientUtil;
import de.pbauerochse.worklogviewer.util.JacksonUtil;
import de.pbauerochse.worklogviewer.version.GitHubVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;

/**
 * Fetches the most recent version of the
 * Worklog Viewer from Github
 */
public class CheckForUpdateTask extends WorklogViewerTask<Optional<GitHubVersion>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckForUpdateTask.class);

    @NotNull
    @Override
    public String getLabel() {
        return getFormatted("task.updatecheck");
    }

    @Override
    public Optional<GitHubVersion> start(@NotNull ProgressCallback progressCallback) {
        progressCallback.setProgress(getFormatted("worker.updatecheck.checking"), 0);

        Optional<GitHubVersion> version = Optional.empty();
        try {
            version = getVersionFromGithub();
        } catch (Exception e) {
            LOGGER.warn("Could not get Version from Github", e);
        } finally {
            progressCallback.setProgress(getFormatted("worker.progress.done"), 100);
        }

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
