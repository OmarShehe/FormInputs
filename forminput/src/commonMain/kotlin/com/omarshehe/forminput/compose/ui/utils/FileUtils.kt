package com.omarshehe.forminput.compose.ui.utils

import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong

public object FileUtils {
    /** True when [size] is known and above [maxSize]; no limit, or a size that is not known, is never too large. */
    public fun isTooLarge(size: Long?, maxSize: Long?): Boolean = size != null && maxSize != null && size > maxSize

    private val units = arrayOf("B", "KB", "MB", "GB", "TB")

    /** `1536` -> `1.5 KB`. Non-positive sizes read as `0 B`. */
    public fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val unit = (log10(size.toDouble()) / log10(1024.0)).toInt().coerceIn(0, units.lastIndex)
        val tenths = (size / 1024.0.pow(unit) * 10).roundToLong()
        return "${tenths / 10}.${tenths % 10} ${units[unit]}"
    }
}
