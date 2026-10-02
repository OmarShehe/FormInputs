package com.omarshehe.forminput.compose.ui.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * `java.util` date helpers kept so existing Android and desktop call sites compile unchanged. They use the device
 * time zone. The pickers themselves use the UTC helpers in `DateExtensions.kt`.
 */
fun String.toDate(format: String = DateUtils.DATE_FORMAT): Date? =
    try {
        SimpleDateFormat(format, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }

fun Date.formatTo(format: String): String = SimpleDateFormat(format, Locale.getDefault()).format(this)

fun Long.formatTo(format: String): String = Date(this).formatTo(format)

fun String.toCalendar(format: String): Calendar? {
    val date = this.toDate(format) ?: return null
    return Calendar.getInstance().apply {
        time = date
    }
}

fun Calendar.formatTo(format: String): String = this.time.formatTo(format)
