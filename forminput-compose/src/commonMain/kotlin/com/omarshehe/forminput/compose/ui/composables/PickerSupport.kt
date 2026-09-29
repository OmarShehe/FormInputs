package com.omarshehe.forminput.compose.ui.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/** Supporting text for a picker: the error while there is one, otherwise the caller's [hint]; null draws nothing. */
internal fun pickerSupportingText(hasError: Boolean, error: String?, hint: String?): (@Composable () -> Unit)? {
    val text = if (hasError && error != null) error else hint
    return text?.let { { Text(it) } }
}
