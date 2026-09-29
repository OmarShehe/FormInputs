package com.omarshehe.forminput.compose.ui.utils

object NumericUtils {
    fun parseDouble(value: String): Double? {
        if (value.isBlank()) return 0.0
        val sanitized = value.replace(",", "").trim()
        return sanitized.toDoubleOrNull()
    }

    fun parseInt(value: String): Int? {
        if (value.isBlank()) return 0
        val sanitized = value.replace(",", "").trim()
        return sanitized.split(".")[0].toIntOrNull()
    }

    fun parseLong(value: String): Long? {
        if (value.isBlank()) return 0L
        val sanitized = value.replace(",", "").trim()
        return sanitized.split(".")[0].toLongOrNull()
    }

    fun formatDecimal(value: Double): String {
        val isNegative = value < 0
        val absValue = if (isNegative) -value else value

        // Round to 2 decimal places
        val totalCents = (absValue * 100.0 + 0.5).toLong()
        val integerPart = totalCents / 100
        val fractionalPart = totalCents % 100

        val fractionalString = if (fractionalPart < 10) "0$fractionalPart" else fractionalPart.toString()
        val sign = if (isNegative && totalCents > 0) "-" else ""

        return "$sign$integerPart.$fractionalString"
    }

    fun formatInteger(value: Double): String {
        val isNegative = value < 0
        val absValue = if (isNegative) -value else value
        val longValue = (absValue + 0.5).toLong()

        if (longValue == 0L) return "0"

        val result = StringBuilder()
        var current = longValue
        var count = 0

        while (current > 0) {
            if (count > 0 && count % 3 == 0) {
                result.append(",")
            }
            result.append(current % 10)
            current /= 10
            count++
        }

        val sign = if (isNegative) "-" else ""
        return sign + result.reverse().toString()
    }
}
