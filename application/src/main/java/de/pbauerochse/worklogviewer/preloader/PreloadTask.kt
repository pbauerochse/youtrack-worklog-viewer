package de.pbauerochse.worklogviewer.preloader

import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.application.Platform
import javafx.scene.web.WebEngine

class PreloadTask: WorklogViewerTask<Unit>(getFormatted("preloader.title")) {

    override fun start(progress: Progress) {
        preloadWebViews(progress.subProgress(100))
    }

    // The WebView component takes about 1.5s once to initialize.
    // Unfortunately that needs to happen on the main JavaFX thread
    // which causes the UI to freeze for that time. By initializing it
    // once during the startup process, it at least is not that obvious
    // and all subsequent calls to the WebView constructor are reasonably fast
    private fun preloadWebViews(subProgress: Progress) {
        subProgress.setProgress(getFormatted("preloader.preloading.webviews"), 1.0)
        Platform.runLater { WebEngine() }
        subProgress.setProgress(getFormatted("preloader.preloading.webviews.done"), 100.0)
    }
}