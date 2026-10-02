package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import com.omarshehe.forminput.compose.ui.libraryString
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.utils.Dimens

public enum class FormInputButtonStyle {
    FILLED,
    OUTLINED,
    TEXT,
}

/** The least width of the filled and outlined button. A `Modifier.width(...)` from the caller replaces it. */
private val DefaultMinWidth = 130.dp

/**
 * A button in the library's look. [style] picks filled, outlined or text; [shape] falls back to the form-wide shape set with
 * [FormInputTheme], then to Material's small shape. A filled or outlined button is at least 130dp wide and [height] tall, and
 * grows with a longer text or a larger font. A width the caller puts on [modifier] (for example `Modifier.width(100.dp)`)
 * replaces the 130dp minimum. While [isLoading] is true the label is replaced by a progress indicator and the click is off.
 */
@Composable
public fun FormInputButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    textRes: StringResource? = null,
    style: FormInputButtonStyle = FormInputButtonStyle.FILLED,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    shape: Shape? = null,
    color: Color? = null,
    contentColor: Color? = null,
    /** The least height of the filled and outlined button; it grows with a larger font size or a longer text. */
    height: Dp = Dimens.fiveGrid,
) {
    val resolvedShape = shape ?: LocalFormInputDefaults.current.shape ?: MaterialTheme.shapes.small

    val buttonText = text ?: textRes?.let { libraryString(it) } ?: ""
    val resolvedColor = color ?: MaterialTheme.colorScheme.primary
    val resolvedContentColor = contentColor ?: MaterialTheme.colorScheme.onPrimary

    when (style) {
        FormInputButtonStyle.FILLED -> {
            Button(
                onClick = onClick,
                modifier = modifier.widthIn(min = DefaultMinWidth).heightIn(min = height),
                enabled = enabled && !isLoading,
                shape = resolvedShape,
                contentPadding = contentPadding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = resolvedColor,
                    contentColor = resolvedContentColor,
                    disabledContainerColor = resolvedColor.copy(alpha = 0.3f),
                    disabledContentColor = resolvedContentColor.copy(alpha = 0.5f),
                ),
            ) {
                ButtonContent(buttonText, icon, isLoading)
            }
        }
        FormInputButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.widthIn(min = DefaultMinWidth).heightIn(min = height),
                enabled = enabled && !isLoading,
                shape = resolvedShape,
                contentPadding = contentPadding,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = resolvedColor,
                ),
                border = BorderStroke(
                    width = Dimens.stroke,
                    color = if (enabled && !isLoading) resolvedColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                ),
            ) {
                ButtonContent(buttonText, icon, isLoading)
            }
        }
        FormInputButtonStyle.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !isLoading,
                shape = resolvedShape,
                contentPadding = contentPadding,
                colors = ButtonDefaults.textButtonColors(contentColor = resolvedColor),
            ) {
                ButtonContent(buttonText, icon, isLoading)
            }
        }
    }
}

@Composable
private fun RowScope.ButtonContent(
    text: String,
    icon: ImageVector?,
    isLoading: Boolean,
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(Dimens.threeGrid),
            strokeWidth = Dimens.quarterGrid,
            color = LocalContentColor.current,
        )
    } else {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null, // decorative: sits beside the button text
                modifier = Modifier.size(Dimens.twoAndHalfGrid),
            )
            if (text.isNotEmpty()) {
                Spacer(Modifier.width(Dimens.oneGrid))
            }
        }
        if (text.isNotEmpty()) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
