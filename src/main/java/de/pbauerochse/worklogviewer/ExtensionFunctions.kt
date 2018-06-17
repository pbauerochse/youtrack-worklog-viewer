package de.pbauerochse.worklogviewer

import java.time.*
import java.time.format.DateTimeFormatter

/**
 * Kotlin convenience extension functions
 */
fun Long.toLocalDate() : LocalDate = toZonedDateTime().toLocalDate()
fun Long.toLocalDateTime() : LocalDateTime = toZonedDateTime().toLocalDateTime()
fun Long.toZonedDateTime() : ZonedDateTime = ZonedDateTime
    .ofInstant(Instant.ofEpochMilli(this), ZoneId.of("UTC"))

fun String.trimToNull() : String? {
    val trimmed = trim()
    return if (trimmed.isNotBlank()) {
        trimmed
    } else {
        null
    }
}

fun String.toLocalDate() : LocalDate? = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
fun LocalDate.toFormattedString() : String = format(DateTimeFormatter.ISO_DATE)

fun String?.isNotNullOrBlank(): Boolean = !this.isNullOrBlank()