package de.pbauerochse.worklogviewer.fx.tasks

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.util.ExceptionUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.version.GitHubVersion
import org.apache.http.Header
import org.apache.http.HttpStatus
import org.apache.http.StatusLine
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.ProxySelector
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
                .max(comparing { it.published })
                .orElse(null)

    private val allGithubReleaseVersions: List<GitHubVersion>
        get() {
            val request = HttpGet("https://api.github.com/repos/pbauerochse/youtrack-worklog-viewer/releases")
            request.addHeader("Accept", "application/json, text/plain, */*")

            try {
                httpClient.use { client ->
                    client.execute(request).use { response ->
                        if (isValidResponseCode(response.statusLine)) {
                            return MAPPER.readValue(response.entity.content, object : TypeReference<List<GitHubVersion>>() {})
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
        get() {
            val config = RequestConfig
                    .custom()
                    .setConnectTimeout(REQUEST_TIMEOUT_IN_MILLIS)
                    .setConnectionRequestTimeout(REQUEST_TIMEOUT_IN_MILLIS)
                    .build()

            return HttpClients.custom()
                    .setDefaultRequestConfig(config)
                    .setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                    .setDefaultHeaders(WEB_BROWSER_HEADERS)
                    .build()
        }

    private fun isValidResponseCode(statusLine: StatusLine?): Boolean {
        if (statusLine == null) throw ExceptionUtil.getIllegalArgumentException("exceptions.main.worker.nullstatus")
        val statusCode = statusLine.statusCode
        return statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CheckForUpdateTask::class.java)
        private val MAPPER = jacksonObjectMapper()
        private const val REQUEST_TIMEOUT_IN_MILLIS = 2000
        private val WEB_BROWSER_HEADERS: List<Header> = listOf(
                BasicHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36"),
                BasicHeader("Accept-Encoding", "gzip, deflate, sdch"),
                BasicHeader("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4")
        )
    }
}
