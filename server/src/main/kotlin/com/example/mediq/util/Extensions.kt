package com.example.mediq.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime

fun <T> MutableStateFlow<T>.update(block: (T) -> T) {
    value = block(value)
}

// kotlinx-datetime 0.6.x dropped the arithmetic members of LocalDateTime, so the
// helpers below delegate to the java.time value that backs it.

fun LocalDateTime.plusMinutes(minutes: Int): LocalDateTime =
    toJavaLocalDateTime().plusMinutes(minutes.toLong()).toKotlinLocalDateTime()

fun LocalDateTime.minusMinutes(minutes: Int): LocalDateTime =
    toJavaLocalDateTime().minusMinutes(minutes.toLong()).toKotlinLocalDateTime()

fun LocalDateTime.minusHours(hours: Int): LocalDateTime =
    toJavaLocalDateTime().minusHours(hours.toLong()).toKotlinLocalDateTime()

fun LocalDateTime.plusHours(hours: Int): LocalDateTime =
    toJavaLocalDateTime().plusHours(hours.toLong()).toKotlinLocalDateTime()

fun LocalDateTime.plusDays(days: Int): LocalDateTime =
    toJavaLocalDateTime().plusDays(days.toLong()).toKotlinLocalDateTime()

fun LocalDateTime.minusDays(days: Int): LocalDateTime =
    toJavaLocalDateTime().minusDays(days.toLong()).toKotlinLocalDateTime()

fun LocalDateTime.withHour(hour: Int): LocalDateTime =
    toJavaLocalDateTime().withHour(hour).toKotlinLocalDateTime()

fun LocalDateTime.withMinute(minute: Int): LocalDateTime =
    toJavaLocalDateTime().withMinute(minute).toKotlinLocalDateTime()

fun LocalDateTime.toLocalDate(): LocalDate = date

fun LocalDateTime.toLocalTime(): LocalTime = time

fun LocalDateTime.toInstant(): Instant = toInstant(TimeZone.UTC)

fun LocalDateTime.isBefore(other: LocalDateTime): Boolean = this < other
