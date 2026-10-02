package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * The typed amount after the price rules, or null when the edit should be refused.
 * Keeps digits and the first dot, drops leading zeros ("007" becomes "7", "0.5" stays), and refuses more than
 * [maxIntegerDigits] whole digits or [maxDecimals] decimals (no dot at all when [maxDecimals] is 0).
 */
fun sanitizeAmount(input: String, maxIntegerDigits: Int, maxDecimals: Int): String? {
    var seenDot = false
    val kept = buildString {
        for (ch in input) {
            when {
                ch.isDigit() -> append(ch)
                ch == '.' && !seenDot && maxDecimals > 0 -> {
                    seenDot = true
                    append(ch)
                }
            }
        }
    }
    val dot = kept.indexOf('.')
    val whole = if (dot < 0) kept else kept.substring(0, dot)
    val fraction = if (dot < 0) "" else kept.substring(dot + 1)
    if (fraction.length > maxDecimals) return null
    val trimmed = whole.trimStart('0')
    if (trimmed.length > maxIntegerDigits) return null
    // A typed "0", "00" or a lone dot still leaves a zero in front, so ".5" reads "0.5".
    val normalisedWhole = if (trimmed.isNotEmpty()) trimmed else if (whole.isNotEmpty() || dot >= 0) "0" else ""

    return if (dot < 0) normalisedWhole else "$normalisedWhole.$fraction"
}

/**
 * This value with its text replaced by [cleaned] (after [sanitizeAmount] dropped or added characters), moving the cursor by the
 * change in length so an edit in the middle does not throw it to the end. Assumes the change happened before the cursor.
 */
fun TextFieldValue.withCleanedText(cleaned: String): TextFieldValue {
    val shift = text.length - cleaned.length
    fun moved(offset: Int) = (offset - shift).coerceIn(0, cleaned.length)
    return copy(text = cleaned, selection = TextRange(moved(selection.start), moved(selection.end)), composition = null)
}

/** Shows the whole part of an amount in groups of three ("1234567.5" as "1,234,567.5") without changing the stored text. */
class ThousandsSeparatorTransformation(private val separator: Char = ',') : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val dot = raw.indexOf('.').let { if (it < 0) raw.length else it }
        val separators = if (dot > 0) (dot - 1) / 3 else 0
        val out = StringBuilder()
        val originalToTransformed = IntArray(raw.length + 1)
        val transformedToOriginal = IntArray(raw.length + separators + 1)
        for (i in raw.indices) {
            if (i in 1 until dot && (dot - i) % 3 == 0) {
                transformedToOriginal[out.length] = i
                out.append(separator)
            }
            originalToTransformed[i] = out.length
            transformedToOriginal[out.length] = i
            out.append(raw[i])
        }
        originalToTransformed[raw.length] = out.length
        transformedToOriginal[out.length] = raw.length
        return TransformedText(
            AnnotatedString(out.toString()),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int) = originalToTransformed[offset.coerceIn(0, raw.length)]
                override fun transformedToOriginal(offset: Int) = transformedToOriginal[offset.coerceIn(0, out.length)]
            },
        )
    }
}
