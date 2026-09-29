package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.clear_search
import com.omarshehe.forminput.compose.resources.search_hint
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.utils.Dimens

@Composable
fun FormInputSearchField(
    state: FormInputTextFieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    }

    Row(
        modifier = modifier
            .height(Dimens.fourAndHalfGrid)
            .clip(RoundedCornerShape(Dimens.oneGrid))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            .border(
                width = Dimens.stroke,
                color = borderColor,
                shape = RoundedCornerShape(Dimens.oneGrid),
            )
            .padding(horizontal = Dimens.twoGrid),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.twoAndHalfGrid),
        )

        Spacer(modifier = Modifier.width(Dimens.oneGrid))

        BasicTextField(
            value = state.value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            interactionSource = interactionSource,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (state.value.isEmpty()) {
                        val placeholder = state.placeholderRes?.let { stringResource(it) } ?: stringResource(Res.string.search_hint)
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                    }
                    innerTextField()
                }
            },
        )

        if (state.value.isNotEmpty()) {
            IconButton(
                onClick = { onValueChange("") },
                modifier = Modifier.size(Dimens.threeGrid),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.clear_search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.twoGrid),
                )
            }
        }
    }
}
