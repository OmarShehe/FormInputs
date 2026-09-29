package com.omarshehe.forminput.compose.ui.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeZoneForSecondsFromGMT

internal actual fun formatUtcMillisPattern(utcMillis: Long, pattern: String): String {
    val formatter = NSDateFormatter().apply {
        dateFormat = pattern
        locale = NSLocale.currentLocale
        timeZone = NSTimeZone.timeZoneForSecondsFromGMT(0)
    }
    return formatter.stringFromDate(NSDate.dateWithTimeIntervalSince1970(utcMillis / 1000.0))
}
