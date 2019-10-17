package de.pbauerochse.worklogviewer.fx.converter

import javafx.util.StringConverter
import java.text.DecimalFormat
import java.util.*

object WorkhoursStringConverter : StringConverter<Float>() {

    private val FORMATTER = DecimalFormat.getInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 2
    }

    override fun toString(value: Float?): String? = value?.let { FORMATTER.format(it) }
    override fun fromString(valueAsString: String?): Float? = valueAsString?.let { FORMATTER.parse(it).toFloat() }

}
