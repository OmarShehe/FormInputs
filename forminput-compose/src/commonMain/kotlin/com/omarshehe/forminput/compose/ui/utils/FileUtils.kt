package com.omarshehe.forminput.compose.ui.utils

import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong

object FileUtils {
    private val units = arrayOf("B", "KB", "MB", "GB", "TB")

    /** `1536` -> `1.5 KB`. Non-positive sizes read as `0 B`. */
    fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val unit = (log10(size.toDouble()) / log10(1024.0)).toInt().coerceIn(0, units.lastIndex)
        val tenths = (size / 1024.0.pow(unit) * 10).roundToLong()
        return "${tenths / 10}.${tenths % 10} ${units[unit]}"
    }
}
