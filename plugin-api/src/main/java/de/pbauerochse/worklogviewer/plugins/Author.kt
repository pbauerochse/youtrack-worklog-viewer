package de.pbauerochse.worklogviewer.plugins

import java.net.URL

/**
 * The author of this plugin
 */
data class Author(

    /**
     * the name of the author
     */
    val name : String,

    /**
     * an optional link to the website of the author or this plugin
     */
    val website : URL? = null

)