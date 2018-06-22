package de.pbauerochse.worklogviewer.report

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class TimeRange(
    val start : LocalDate,
    val end : LocalDate) {

    val reportName : String by lazy {
        val formatter = DateTimeFormatter.ISO_DATE
        "${start.format(formatter)}_${end.format(formatter)}"
    }

}
