package de.pbauerochse.worklogviewer

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

fun Long.toLocalDateUsingUserTimeZone(): LocalDate = toLocalDateTimeUsingUserTimeZone().toLocalDate()

fun Long.toLocalDateTimeUsingUserTimeZone(): LocalDateTime = ZonedDateTime
    .ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    .toLocalDateTime()

fun Long.toLocalDate() : LocalDate = toLocalDateTime()
    .toLocalDate()

fun Long.toLocalDateTime() : LocalDateTime = ZonedDateTime
    .ofInstant(Instant.ofEpochMilli(this), ZoneId.of("UTC"))
    .toLocalDateTime()

fun LocalDate.isSameDayOrAfter(other: LocalDate): Boolean = isSameDay(other) || isAfter(other)

fun LocalDate.isSameDayOrBefore(other: LocalDate): Boolean = isSameDay(other) || isBefore(other)

fun LocalDate.isSameDay(other: LocalDate) = isEqual(other)