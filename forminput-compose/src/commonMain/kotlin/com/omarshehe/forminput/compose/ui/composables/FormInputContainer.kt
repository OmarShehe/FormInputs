package com.omarshehe.forminput.compose.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.omarshehe.forminput.compose.ui.LocalFormInputDefaults
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.utils.Dimens

/**
 * The box drawn around the stepper and the upload inputs, so they follow the same [style], [shape] and [colors] as the text fields.
 *
 * With no style (argument or form-wide default) and no [colors] it draws the input's own look: [defaultBorder] and
 * [defaultBackground]. [FormInputFieldStyle.OUTLINED] draws an outline; [FormInputFieldStyle.FILLED] a tinted box with an underline.
 * Container and indicator colours come from [colors] (the unfocused ones, or the error ones while [hasError]).
 */
@Composable
internal fun Modifier.formInputContainer(
    shape: Shape,
    style: FormInputFieldStyle?,
    colors: TextFieldColors?,
    hasError: Boolean,
    defaultBorder: Color,
    defaultBackground: Color,
): Modifier {
    val effectiveStyle = style ?: LocalFormInputDefaults.current.style
    val fieldColors = colors ?: when (effectiveStyle) {
        FormInputFieldStyle.FILLED -> TextFieldDefaults.colors()
        FormInputFieldStyle.OUTLINED -> OutlinedTextFieldDefaults.colors()
        null -> null
    }
    val indicator = when {
        fieldColors != null -> if (hasError) fieldColors.errorIndicatorColor else fieldColors.unfocusedIndicatorColor
        hasError -> MaterialTheme.colorScheme.error
        else -> defaultBorder
    }
    val container = when {
        fieldColors != null -> if (hasError) fieldColors.errorContainerColor else fieldColors.unfocusedContainerColor
        else -> defaultBackground
    }
    val underline = Dimens.stroke
    return if (effectiveStyle == FormInputFieldStyle.FILLED) {
        this
            .clip(shape)
            .background(container)
            .drawBehind {
                val y = size.height - underline.toPx() / 2
                drawLine(indicator, Offset(0f, y), Offset(size.width, y), strokeWidth = underline.toPx())
            }
    } else {
        this
            .clip(shape)
            .border(width = Dimens.stroke, color = indicator, shape = shape)
            .background(container)
    }
}
