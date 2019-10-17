package de.pbauerochse.worklogviewer.fx.listener

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.DatePicker
import org.slf4j.LoggerFactory

/**
 * Workaround for the fact, that the [javafx.scene.control.DatePicker]
 * value does not get updated, when the value of the DatePicker field
 * is manually edited (not using the calendar popup)
 *
 * see https://stackoverflow.com/questions/32346893/javafx-datepicker-not-updating-value?rq=1
 */
class DatePickerManualEditListener(private val datePicker: DatePicker) : ChangeListener<Boolean> {

    override fun changed(observable: ObservableValue<out Boolean>, oldValue: Boolean?, newValue: Boolean?) {
        if (newValue != null && !newValue) {
            LOGGER.debug("Focus lost on $datePicker")
            datePicker.value = datePicker.converter.fromString(datePicker.editor.text)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DatePickerManualEditListener::class.java)

        fun applyTo(datePicker: DatePicker) {
            datePicker.focusedProperty().addListener(DatePickerManualEditListener(datePicker))
        }
    }
}