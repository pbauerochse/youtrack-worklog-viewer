package de.pbauerochse.worklogviewer.fx.tasks

import com.fasterxml.jackson.core.type.TypeReference
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.util.HttpClientUtil
import de.pbauerochse.worklogviewer.util.JacksonUtil
import de.pbauerochse.worklogviewer.version.GitHubVersion
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.StringReader
import java.util.*
import java.util.Comparator.comparing

/**
 * Fetches the most recent version of the
 * Worklog Viewer from Github
 */
class CheckForUpdateTask : WorklogViewerTask<GitHubVersion?>(getFormatted("task.updatecheck")) {

    override fun start(progress: Progress): GitHubVersion? {
        progress.setProgress(getFormatted("worker.updatecheck.checking"), 0.1)
        val version = mostRecentGithubReleaseVersion

        progress.setProgress(getFormatted("worker.progress.done"), 100)
        return version
    }

    private val mostRecentGithubReleaseVersion: GitHubVersion?
        get() = allGithubReleaseVersions.stream()
            .filter { it.isRelease }
            .max(comparing<GitHubVersion, Date> { it.published })
            .orElse(null)

    private val allGithubReleaseVersions: List<GitHubVersion>
        get() {
            val request = HttpGet("https://api.github.com/repos/pbauerochse/youtrack-worklog-viewer/releases")
            request.addHeader("Accept", "application/json, text/plain, */*")

            try {
                httpClient.use { client ->
                    client.execute(request).use { response ->
                        if (HttpClientUtil.isValidResponseCode(response.statusLine)) {
                            val jsonResponse = EntityUtils.toString(response.entity)
                            return JacksonUtil.parseValue(StringReader(jsonResponse), object : TypeReference<List<GitHubVersion>>() {

                            })
                        }

                        LOGGER.warn("Could not get most recent version: {}", response.statusLine.reasonPhrase)
                        EntityUtils.consumeQuietly(response.entity)
                    }
                }
            } catch (e: IOException) {
                throw ExceptionUtil.getIllegalStateException("exceptions.updater.versions", e)
            }

            return emptyList()
        }

    private val httpClient: CloseableHttpClient
        get() = HttpClientUtil.getDefaultClientBuilder(1)
            .setDefaultHeaders(HttpClientUtil.getRegularBrowserHeaders())
            .build()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CheckForUpdateTask::class.java)
    }
}
