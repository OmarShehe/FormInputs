package com.omarshehe.forminput.compose.ui.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal actual fun formatUtcMillisPattern(utcMillis: Long, pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date(utcMillis))
