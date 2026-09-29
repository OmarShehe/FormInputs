package com.omarshehe.forminput.compose.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.omarshehe.forminput.compose.ui.utils.Dimens

/**
 * Text under a picker or dropdown: the error while there is one, otherwise the caller's [hint]; nothing when both are null.
 * It is drawn below the field, not inside it, so the field's own box stays exactly the size of its outline and a tap layer
 * can cover it at any font size.
 */
@Composable
internal fun PickerSupportingText(hasError: Boolean, error: String?, hint: String?) {
    val showsError = hasError && error != null
    val text = if (showsError) error else hint
    if (text != null) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (showsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = Dimens.twoGrid, top = Dimens.halfGrid, end = Dimens.twoGrid),
        )
    }
}
