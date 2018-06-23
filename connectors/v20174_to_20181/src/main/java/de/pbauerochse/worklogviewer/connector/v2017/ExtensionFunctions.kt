package de.pbauerochse.worklogviewer.connector.v2017

import java.time.*

fun String.trimToNull() : String? {
    val trimmed = trim()
    return if (trimmed.isNotBlank()) {
        trimmed
    } else {
        null
    }
}

fun LocalDate.utcEpochMillisAtStartOfDay(): Long = this
    .atStartOfDay(ZoneId.of("UTC"))
    .toInstant()
    .toEpochMilli()

fun LocalDate.utcEpochMillisAtEndOfDay(): Long = this
    .atStartOfDay(ZoneId.of("UTC"))
    .plusDays(1)
    .minusNanos(1)
    .toInstant()
    .toEpochMilli()

fun Long.toLocalDate() : LocalDate = toLocalDateTime()
    .toLocalDate()

fun Long.toLocalDateTime() : LocalDateTime = ZonedDateTime
    .ofInstant(Instant.ofEpochMilli(this), ZoneId.of("UTC"))
    .toLocalDateTime()