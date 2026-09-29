package com.omarshehe.forminput.compose.ui.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

object DateUtils {
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val TIME_FORMAT = "HH:mm"
    const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"
    const val DATE_DISPLAY_FORMAT = "dd MMM yyyy"
    const val DATE_TIME_DISPLAY_FORMAT = "dd MMM yyyy  HH:mm"
}

/**
 * Formats [utcMillis] with a `SimpleDateFormat`-style [pattern] (`yyyy`, `MMM`, `dd`, `HH`, `mm`) in UTC, using the
 * device language for names such as month abbreviations. Only the display formats need this; stored values use the
 * fixed formats above.
 */
internal expect fun formatUtcMillisPattern(utcMillis: Long, pattern: String): String

private const val MILLIS_PER_MINUTE = 60_000L
private val timeRegex = Regex("^(\\d{2}):(\\d{2})$")
private val dateTimeRegex = Regex("^(\\d{4}-\\d{2}-\\d{2}) (\\d{2}):(\\d{2})$")

/** Parses `yyyy-MM-dd` into UTC-midnight millis, which is what Material date pickers select and expect. */
fun String.dateToUtcMillis(): Long? =
    try {
        LocalDate.parse(trim()).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    } catch (e: IllegalArgumentException) {
        null
    }

/** The `yyyy-MM-dd` date of [this] in UTC. */
fun Long.utcMillisToDate(): String = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date.toString()

fun Long.utcYear(): Int = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).year

fun todayUtcMidnightMillis(): Long = nowMillis().utcMillisToDate().dateToUtcMillis() ?: 0L

internal fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()

/** The device's current local hour and minute. */
internal fun currentLocalHourMinute(): Pair<Int, Int> {
    val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return time.hour to time.minute
}

/** Parses `HH:mm` into (hour, minute), or null when it is not a valid 24-hour time. */
fun String.toHourMinute(): Pair<Int, Int>? {
    val match = timeRegex.matchEntire(this) ?: return null
    val hour = match.groupValues[1].toInt()
    val minute = match.groupValues[2].toInt()
    return if (hour in 0..23 && minute in 0..59) hour to minute else null
}

fun formatHourMinute(hour: Int, minute: Int): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

data class DateTimeParts(val dateUtcMillis: Long, val hour: Int, val minute: Int)

/** Parses `yyyy-MM-dd HH:mm`. */
fun String.toDateTimeParts(): DateTimeParts? {
    val match = dateTimeRegex.matchEntire(this) ?: return null
    val date = match.groupValues[1].dateToUtcMillis() ?: return null
    val (hour, minute) = "${match.groupValues[2]}:${match.groupValues[3]}".toHourMinute() ?: return null
    return DateTimeParts(date, hour, minute)
}

fun formatDateTime(dateUtcMillis: Long, hour: Int, minute: Int): String =
    "${dateUtcMillis.utcMillisToDate()} ${formatHourMinute(hour, minute)}"

/** [dateUtcMillis] plus a time of day, as UTC millis (used for the display format). */
internal fun dateTimeUtcMillis(dateUtcMillis: Long, hour: Int, minute: Int): Long =
    dateUtcMillis + (hour * 60L + minute) * MILLIS_PER_MINUTE
