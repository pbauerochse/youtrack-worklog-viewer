package de.pbauerochse.worklogviewer.fx.tasks;

import com.fasterxml.jackson.core.type.TypeReference;
import de.pbauerochse.worklogviewer.tasks.Progress;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.HttpClientUtil;
import de.pbauerochse.worklogviewer.util.JacksonUtil;
import de.pbauerochse.worklogviewer.version.GitHubVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;
import static java.util.Comparator.comparing;

/**
 * Fetches the most recent version of the
 * Worklog Viewer from Github
 */
public class CheckForUpdateTask extends WorklogViewerTask<Optional<GitHubVersion>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckForUpdateTask.class);

    public CheckForUpdateTask() {
        super(getFormatted("task.updatecheck"));
    }

    @Override
    @Nullable
    public Optional<GitHubVersion> start(@NotNull Progress progress) {
        progress.setProgress(getFormatted("worker.updatecheck.checking"), 0.1);
        Optional<GitHubVersion> versionOptional = getVersionFromGithub();

        progress.setProgress(getFormatted("worker.progress.done"), 100);
        return versionOptional;
    }

    private Optional<GitHubVersion> getVersionFromGithub() {
        return getAllGithubReleaseVersions().stream()
                .filter(GitHubVersion::isRelease)
                .max(comparing(GitHubVersion::getPublished));
    }

    private List<GitHubVersion> getAllGithubReleaseVersions() {
        HttpGet request = new HttpGet("https://api.github.com/repos/pbauerochse/youtrack-worklog-viewer/releases");
        request.addHeader("Accept", "application/json, text/plain, */*");

        try (CloseableHttpClient client = getHttpClient()) {
            try (CloseableHttpResponse response = client.execute(request)) {
                if (HttpClientUtil.isValidResponseCode(response.getStatusLine())) {
                    String jsonResponse = EntityUtils.toString(response.getEntity());
                    return JacksonUtil.parseValue(new StringReader(jsonResponse), new TypeReference<>() {
                    });
                }

                LOGGER.warn("Could not get most recent version: {}", response.getStatusLine().getReasonPhrase());
                EntityUtils.consumeQuietly(response.getEntity());
            }
        } catch (IOException e) {
            throw ExceptionUtil.getIllegalStateException("exceptions.updater.versions", e);
        }

        return Collections.emptyList();
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClientUtil.getDefaultClientBuilder(1)
                .setDefaultHeaders(HttpClientUtil.getRegularBrowserHeaders())
                .build();
    }
}
