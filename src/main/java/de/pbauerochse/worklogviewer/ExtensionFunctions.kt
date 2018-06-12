package de.pbauerochse.worklogviewer

import java.time.*

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

fun String?.isNotNullOrBlank(): Boolean = !this.isNullOrBlank()