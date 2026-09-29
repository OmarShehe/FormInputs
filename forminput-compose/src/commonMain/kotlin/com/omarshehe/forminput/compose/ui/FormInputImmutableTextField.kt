package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omarshehe.forminput.compose.ui.composables.FormInputBoxField
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextOverflow
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.utils.Dimens

@Composable
fun FormInputImmutableTextField(
    modifier: Modifier = Modifier,
    label: String? = null,
    text: String? = null,
    imageVector: ImageVector? = null,
    isError: Boolean = false,
    outlined: Boolean = false,
    shape: Shape? = null,
    onIconClick: () -> Unit = {},
    style: FormInputFieldStyle? = null,
) {
    // `style` wins; without it the older `outlined` flag chooses between outlined and the flat underlined look.
    val defaults = LocalFormInputDefaults.current
    val effectiveStyle = style ?: defaults.style ?: if (outlined) FormInputFieldStyle.OUTLINED else null
    val fieldShape = shape ?: defaults.shape ?: when (effectiveStyle) {
        FormInputFieldStyle.OUTLINED -> OutlinedTextFieldDefaults.shape
        FormInputFieldStyle.FILLED -> TextFieldDefaults.shape
        null -> MaterialTheme.shapes.medium.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)
    }
    if (effectiveStyle != null) {
        val filled = effectiveStyle == FormInputFieldStyle.FILLED
        FormInputBoxField(
            value = TextFieldValue(text.orEmpty()),
            onValueChange = {},
            filled = filled,
            shape = fieldShape,
            colors = if (filled) TextFieldDefaults.colors() else OutlinedTextFieldDefaults.colors(),
            modifier = modifier.pointerHoverIcon(PointerIcon.Default, overrideDescendants = true),
            readOnly = true,
            focusable = false,
            isError = isError,
            label = label?.let { { Text(it) } },
            trailingIcon = imageVector?.let {
                {
                    IconButton(onClick = onIconClick) {
                        Icon(it, contentDescription = label, tint = if (isError) colorScheme.error else colorScheme.secondary)
                    }
                }
            },
        )
    } else {
        val borderColor = if (isError) colorScheme.error else colorScheme.onSurface.copy(alpha = 0.42f)

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(color = colorScheme.surface, shape = fieldShape),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = Dimens.twoGrid,
                            end = if (imageVector != null) Dimens.default else Dimens.twoGrid,
                            top = Dimens.oneAndHalfGrid,
                            bottom = Dimens.oneAndHalfGrid,
                        ),
                ) {
                    label?.takeUnless { it.isBlank() }?.let {
                        Text(
                            text = it,
                            style = typography.bodySmall.copy(color = colorScheme.onSurface.copy(alpha = 0.6f)),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    text?.takeUnless { it.isBlank() }?.let {
                        Text(text = it, style = typography.bodyMedium.copy(color = colorScheme.onSurface))
                    }
                }
                imageVector?.let {
                    IconButton(onClick = onIconClick, modifier = Modifier.padding(vertical = Dimens.halfGrid)) {
                        Icon(it, contentDescription = label, tint = if (isError) colorScheme.error else colorScheme.secondary)
                    }
                }
            }
            HorizontalDivider(color = borderColor)
        }
    }
}
