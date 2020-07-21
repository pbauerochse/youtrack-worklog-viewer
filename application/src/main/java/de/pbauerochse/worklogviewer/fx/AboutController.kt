package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.setHref
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Hyperlink
import java.net.URL
import java.util.*

/**
 * Controller for the "About" Dialog
 */
class AboutController : Initializable {

    @FXML
    private lateinit var youtrackLink: Hyperlink

    @FXML
    private lateinit var worklogViewerLink: Hyperlink

    @FXML
    private lateinit var logoByPatrickMarx: Hyperlink

    @FXML
    private lateinit var licenseLink: Hyperlink

    @FXML
    private lateinit var iconsLink: Hyperlink

    override fun initialize(location: URL, resources: ResourceBundle) {
        youtrackLink.setHref("https://www.jetbrains.com/youtrack/")
        worklogViewerLink.setHref("https://github.com/pbauerochse/youtrack-worklog-viewer")
        logoByPatrickMarx.setHref("https://twitter.com/ptrckmrx")
        licenseLink.setHref("https://github.com/pbauerochse/youtrack-worklog-viewer/blob/master/LICENSE.txt")
        iconsLink.setHref("http://www.famfamfam.com/lab/icons/silk/")
    }

}
