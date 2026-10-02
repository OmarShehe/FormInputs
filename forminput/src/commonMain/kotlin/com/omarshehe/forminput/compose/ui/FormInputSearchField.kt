package com.omarshehe.forminput.compose.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.clear_search
import com.omarshehe.forminput.compose.resources.search_hint
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.composables.resolvedPlaceholder
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.utils.Dimens

/** Search field driven by [FormInputTextFieldState.value]; use the [TextFieldValue] overload to control the cursor. */
@Composable
public fun FormInputSearchField(
    state: FormInputTextFieldState,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: FormInputFieldStyle? = null,
    shape: Shape? = null,
    colors: TextFieldColors? = null,
    enabled: Boolean = true,
    onSearch: ((String) -> Unit)? = null,
    textStyle: TextStyle? = null,
    contentPadding: PaddingValues? = null,
) {
    var textValueState by remember { mutableStateOf(TextFieldValue(text = state.value)) }
    LaunchedEffect(state.value) {
        if (textValueState.text != state.value) {
            textValueState = textValueState.copy(text = state.value, selection = TextRange(state.value.length))
        }
    }
    FormInputSearchField(
        state = state,
        value = textValueState,
        onValueChange = { newValue ->
            textValueState = newValue
            onValueChange(newValue.text)
        },
        modifier = modifier,
        style = style,
        shape = shape,
        colors = colors,
        enabled = enabled,
        onSearch = onSearch,
        textStyle = textStyle,
        contentPadding = contentPadding,
    )
}

/**
 * Search field with a hoisted [TextFieldValue], so the caller owns the text, cursor and selection.
 *
 * With no [style] it keeps the compact pill look. Pass [FormInputFieldStyle.OUTLINED] or [FormInputFieldStyle.FILLED] to
 * draw it like the other form inputs; [shape] and [colors] then apply as they do on the text field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun FormInputSearchField(
    state: FormInputTextFieldState,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    style: FormInputFieldStyle? = null,
    shape: Shape? = null,
    colors: TextFieldColors? = null,
    enabled: Boolean = true,
    onSearch: ((String) -> Unit)? = null,
    textStyle: TextStyle? = null,
    contentPadding: PaddingValues? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val placeholderText = state.resolvedPlaceholder() ?: formInputString(FormInputStrings::searchHint, Res.string.search_hint)

    val defaults = LocalFormInputDefaults.current
    val effectiveStyle = style ?: defaults.style
    if (effectiveStyle != null) {
        val filled = effectiveStyle == FormInputFieldStyle.FILLED
        val fieldColors = colors ?: if (filled) TextFieldDefaults.colors() else OutlinedTextFieldDefaults.colors()
        val fieldShape = shape ?: defaults.shape ?: if (filled) TextFieldDefaults.shape else OutlinedTextFieldDefaults.shape
        // A search field has no floating label: it would sit over the placeholder while focused.
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = if (filled) TextFieldDefaults.MinHeight else OutlinedTextFieldDefaults.MinHeight),
            interactionSource = interactionSource,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface).merge(textStyle ?: MaterialTheme.typography.bodyLarge),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke(value.text) }),
            decorationBox = { innerTextField ->
                val placeholderSlot: @Composable () -> Unit = { Text(placeholderText, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                val leadingSlot: @Composable () -> Unit = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) // decorative: the placeholder names the field
                }
                val trailingSlot: (@Composable () -> Unit)? = if (value.text.isNotEmpty() && enabled) {
                    {
                        IconButton(onClick = { onValueChange(TextFieldValue("")) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = formInputString(FormInputStrings::clearSearch, Res.string.clear_search),
                                modifier = Modifier.size(Dimens.twoAndHalfGrid),
                            )
                        }
                    }
                } else {
                    null
                }
                if (filled) {
                    TextFieldDefaults.DecorationBox(
                        value = value.text,
                        innerTextField = innerTextField,
                        enabled = enabled,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        isError = state.hasError,
                        placeholder = placeholderSlot,
                        leadingIcon = leadingSlot,
                        trailingIcon = trailingSlot,
                        shape = fieldShape,
                        colors = fieldColors,
                        contentPadding = contentPadding ?: TextFieldDefaults.contentPaddingWithoutLabel(),
                        container = {
                            TextFieldDefaults.Container(
                                enabled = enabled,
                                isError = state.hasError,
                                interactionSource = interactionSource,
                                colors = fieldColors,
                                shape = fieldShape,
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
                        isError = state.hasError,
                        placeholder = placeholderSlot,
                        leadingIcon = leadingSlot,
                        trailingIcon = trailingSlot,
                        colors = fieldColors,
                        contentPadding = contentPadding ?: OutlinedTextFieldDefaults.contentPadding(),
                        container = {
                            OutlinedTextFieldDefaults.Container(
                                enabled = enabled,
                                isError = state.hasError,
                                interactionSource = interactionSource,
                                colors = fieldColors,
                                shape = fieldShape,
                            )
                        },
                    )
                }
            },
        )
        return
    }
    val pillShape = shape ?: defaults.shape ?: RoundedCornerShape(Dimens.oneGrid)

    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    }

    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .heightIn(min = Dimens.sixGrid)
            .clip(pillShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
            .border(
                width = Dimens.stroke,
                color = borderColor,
                shape = pillShape,
            )
            .padding(contentPadding ?: PaddingValues(horizontal = Dimens.twoGrid)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null, // decorative: the placeholder names the field
            tint = if (isFocused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(Dimens.twoAndHalfGrid),
        )

        Spacer(modifier = Modifier.width(Dimens.oneGrid))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            interactionSource = interactionSource,
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface).merge(textStyle ?: MaterialTheme.typography.bodyMedium),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke(value.text) }),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.text.isEmpty()) {
                        Text(
                            text = placeholderText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                    }
                    innerTextField()
                }
            },
        )

        if (value.text.isNotEmpty() && enabled) {
            IconButton(
                onClick = { onValueChange(TextFieldValue("")) },
                modifier = Modifier.size(Dimens.threeGrid),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = formInputString(FormInputStrings::clearSearch, Res.string.clear_search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.twoGrid),
                )
            }
        }
    }
}
