package com.omarshehe.forminput.compose.ui.utils

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal actual fun formatUtcMillisPattern(utcMillis: Long, pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date(utcMillis))

internal actual fun parseUtcMillisPattern(text: String, pattern: String): Long? {
    val format = SimpleDateFormat(pattern, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
        isLenient = false
    }
    val position = ParsePosition(0)
    val date = format.parse(text, position) ?: return null
    return if (position.index == text.length) date.time else null
}
