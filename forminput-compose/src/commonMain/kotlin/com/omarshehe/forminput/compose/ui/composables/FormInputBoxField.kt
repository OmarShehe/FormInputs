package com.omarshehe.forminput.compose.ui.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.focusable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

/**
 * The single-line boxed field behind the dropdown, the pickers, the colour picker and the read-only field.
 *
 * It draws Material's outlined or filled decoration around a plain `BasicTextField`, the same way [com.omarshehe.forminput.compose.ui.FormInputTextField]
 * does, instead of using Material's `OutlinedTextField` / `TextField`. Those reserve space above their border for a floating label
 * (a gap that is not the font's to measure, so it drifted at large font sizes); this field has none, so its layout is exactly the
 * drawn box and a tap layer or ripple laid over it lines up at any font size. [focusable] false makes a read-only field that a
 * tap layer opens instead of focusing.
 */
@Composable
internal fun FormInputBoxField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    filled: Boolean,
    shape: Shape,
    colors: TextFieldColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    focusable: Boolean = true,
    isError: Boolean = false,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val textColor = when {
        !enabled -> colors.disabledTextColor
        isError -> colors.errorTextColor
        focused -> colors.focusedTextColor
        else -> colors.unfocusedTextColor
    }
    val focusModifier = if (focusable) Modifier else Modifier.focusProperties { canFocus = false }.focusable(false)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .then(focusModifier)
            .fillMaxWidth()
            .defaultMinSize(minHeight = TextFieldDefaults.MinHeight),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(color = textColor),
        cursorBrush = SolidColor(if (isError) colors.errorCursorColor else colors.cursorColor),
        singleLine = true,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            if (filled) {
                TextFieldDefaults.DecorationBox(
                    value = value.text,
                    innerTextField = innerTextField,
                    enabled = enabled,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isError,
                    label = label,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    shape = shape,
                    colors = colors,
                    contentPadding = if (label != null) {
                        TextFieldDefaults.contentPaddingWithLabel()
                    } else {
                        TextFieldDefaults.contentPaddingWithoutLabel()
                    },
                    container = {
                        TextFieldDefaults.Container(
                            enabled = enabled,
                            isError = isError,
                            interactionSource = interactionSource,
                            colors = colors,
                            shape = shape,
                        )
                    },
                )
            } else {
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value.text,
                    innerTextField = innerTextField,
                    enabled = enabled,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isError,
                    label = label,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    colors = colors,
                    contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = enabled,
                            isError = isError,
                            interactionSource = interactionSource,
                            colors = colors,
                            shape = shape,
                        )
                    },
                )
            }
        },
    )
}
