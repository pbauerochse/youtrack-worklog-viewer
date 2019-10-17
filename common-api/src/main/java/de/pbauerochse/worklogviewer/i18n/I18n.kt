package de.pbauerochse.worklogviewer.i18n

import java.text.MessageFormat
import java.util.*

/**
 * Can be used to generate translated messages
 */
class I18n(bundle : String) {

    private val resource = ResourceBundle.getBundle(bundle)

    fun get(key : String) : String = resource.getString(key)

    fun get(key : String, vararg params : Any) : String =
            MessageFormat.format(get(key), *params)

}