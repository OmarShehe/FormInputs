package com.omarshehe.forminput.compose.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.ui.model.FormInputState
import com.omarshehe.forminput.compose.ui.utils.Dimens
import org.jetbrains.compose.resources.stringResource

/** The glyph beside a header action label; it is label-sized, so it is not meant to change. */
private val HeaderActionIconSize = 14.dp

/**
 * The small action button above a field, such as "Clear" or "Reload": drawn from [FormInputState.onHeaderActionClick],
 * [FormInputState.headerActionLabelRes] and [FormInputState.headerActionIcon], and nothing at all while no click handler is set.
 * Shared by every field whose state carries a header action, so they all look and behave the same.
 */
@Composable
internal fun FormInputHeaderAction(state: FormInputState) {
    val onClick = state.onHeaderActionClick ?: return
    val icon = state.headerActionIcon
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(Dimens.default),
            modifier = Modifier.height(Dimens.threeGrid),
        ) {
            if (icon != null) {
                Icon(icon, null, modifier = Modifier.size(HeaderActionIconSize)) // decorative: the action label beside it names it
                Spacer(Modifier.width(Dimens.halfGrid))
            }
            state.headerActionLabelRes?.let {
                Text(stringResource(it), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}
