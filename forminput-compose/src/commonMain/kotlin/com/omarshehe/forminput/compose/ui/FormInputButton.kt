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

enum class FormInputButtonStyle {
    FILLED,
    OUTLINED,
    TEXT,
}

@Composable
fun FormInputButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    textRes: StringResource? = null,
    style: FormInputButtonStyle = FormInputButtonStyle.FILLED,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    shape: Shape = MaterialTheme.shapes.small,
    color: Color? = null,
    contentColor: Color? = null,
    height: Dp = Dimens.fiveGrid,
) {
    val buttonText = text ?: textRes?.let { libraryString(it) } ?: ""
    val resolvedColor = color ?: MaterialTheme.colorScheme.primary
    val resolvedContentColor = contentColor ?: MaterialTheme.colorScheme.onPrimary

    when (style) {
        FormInputButtonStyle.FILLED -> {
            Button(
                onClick = onClick,
                modifier = modifier.widthIn(min = 130.dp).height(height),
                enabled = enabled && !isLoading,
                shape = shape,
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
                modifier = modifier.widthIn(min = 130.dp).height(height),
                enabled = enabled && !isLoading,
                shape = shape,
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
                shape = shape,
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
                contentDescription = null,
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
