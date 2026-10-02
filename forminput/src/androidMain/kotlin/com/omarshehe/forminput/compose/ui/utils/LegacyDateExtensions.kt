package com.omarshehe.forminput.compose.ui.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * `java.util` date helpers kept so existing Android and desktop call sites compile unchanged. They use the device
 * time zone. The pickers themselves use the UTC helpers in `DateExtensions.kt`.
 */
public fun String.toDate(format: String = DateUtils.DATE_FORMAT): Date? =
    try {
        SimpleDateFormat(format, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }

public fun Date.formatTo(format: String): String = SimpleDateFormat(format, Locale.getDefault()).format(this)

public fun Long.formatTo(format: String): String = Date(this).formatTo(format)

public fun String.toCalendar(format: String): Calendar? {
    val date = this.toDate(format) ?: return null
    return Calendar.getInstance().apply {
        time = date
    }
}

public fun Calendar.formatTo(format: String): String = this.time.formatTo(format)
