package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.hide_password
import com.omarshehe.forminput.compose.resources.show_password
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.composables.resolvedError
import com.omarshehe.forminput.compose.ui.composables.resolvedLabel
import com.omarshehe.forminput.compose.ui.composables.resolvedPlaceholder
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.Symbols

/**
 * Text field driven by [FormInputTextFieldState.value]. The cursor and selection are kept internally; use the
 * overload that takes a [TextFieldValue] to control them.
 */
@Composable
fun FormInputTextField(
    state: FormInputTextFieldState,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    colors: TextFieldColors? = null,
    shape: Shape? = null,
    contentPadding: PaddingValues? = null,
    style: FormInputFieldStyle? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle? = null,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onValueChange: (FormInputTextFieldState) -> Unit = {},
) {
    var textValueState by remember { mutableStateOf(TextFieldValue(text = state.value)) }
    LaunchedEffect(state.value) {
        if (textValueState.text != state.value) {
            textValueState = textValueState.copy(text = state.value, selection = TextRange(state.value.length))
        }
    }
    FormInputTextField(
        state = state,
        value = textValueState,
        onValueChange = { newValue ->
            textValueState = newValue
            onValueChange(state.copy(value = newValue.text))
        },
        modifier = modifier,
        textModifier = textModifier,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        style = style,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
    )
}

/**
 * Text field with a hoisted [TextFieldValue], so the caller owns the text, cursor and selection. Input is filtered
 * by the state's type, [FormInputTextFieldState.maxChar] and [FormInputTextFieldState.autoCapitalize] before
 * [onValueChange] is called; [FormInputTextFieldState.value] is not used by this overload.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputTextField(
    state: FormInputTextFieldState,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    colors: TextFieldColors? = null,
    shape: Shape? = null,
    contentPadding: PaddingValues? = null,
    style: FormInputFieldStyle? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle? = null,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val defaults = LocalFormInputDefaults.current
    val filled = (style ?: defaults.style) == FormInputFieldStyle.FILLED
    val fieldColors = colors ?: if (filled) TextFieldDefaults.colors() else OutlinedTextFieldDefaults.colors()
    val fieldShape = shape ?: defaults.shape ?: if (filled) TextFieldDefaults.shape else OutlinedTextFieldDefaults.shape
    val labelText = state.resolvedLabel()
    val placeholderText = state.resolvedPlaceholder()
    val errorText = state.resolvedError()
    var passwordVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val effectiveTransformation = visualTransformation ?: if (state.type == FormInputType.PASSWORD && !passwordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (state.onHeaderActionClick != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = state.onHeaderActionClick,
                    contentPadding = PaddingValues(Dimens.default),
                    modifier = Modifier.height(Dimens.threeGrid),
                ) {
                    if (state.headerActionIcon != null) {
                        Icon(state.headerActionIcon, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(Dimens.halfGrid))
                    }
                    state.headerActionLabelRes?.let {
                        Text(stringResource(it), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        BasicTextField(
            value = value,
            onValueChange = { newValue -> state.sanitize(newValue)?.let(onValueChange) },
            modifier = textModifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle ?: MaterialTheme.typography.bodyLarge.copy(
                color = if (enabled) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.38f),
            ),
            keyboardOptions = keyboardOptions ?: getKeyboardOptions(state),
            keyboardActions = keyboardActions,
            visualTransformation = effectiveTransformation,
            singleLine = state.isSingleLine,
            minLines = state.minLines,
            maxLines = state.maxLines,
            cursorBrush = SolidColor(colorScheme.primary),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                val labelSlot: (@Composable () -> Unit)? = labelText?.let {
                    {
                        Text(
                            text = buildAnnotatedString {
                                append(it)
                                if (state.isMandatory) {
                                    withStyle(SpanStyle(color = colorScheme.error)) {
                                        append(" ${Symbols.MANDATORY_SYMBOL}")
                                    }
                                }
                            },
                        )
                    }
                }
                val placeholderSlot: (@Composable () -> Unit)? = placeholderText?.let { { Text(it) } }
                val leadingSlot: (@Composable () -> Unit)? = leadingIcon ?: state.icon?.let {
                    { Icon(imageVector = it, contentDescription = null, tint = colorScheme.primary) }
                }
                // A caller-supplied visualTransformation means the caller handles masking, so the built-in password toggle steps aside.
                val trailingSlot = trailingIcon ?: if (visualTransformation != null) null else getTrailingIcon(state, passwordVisible) { passwordVisible = !passwordVisible }
                val prefixSlot: (@Composable () -> Unit)? = (state.prefix ?: state.prefixRes?.let { stringResource(it) })?.let { { Text(it, color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) } }
                val suffixSlot: (@Composable () -> Unit)? = (state.suffix ?: state.suffixRes?.let { stringResource(it) })?.let { { Text(it, color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) } }
                if (filled) {
                    TextFieldDefaults.DecorationBox(
                        value = value.text,
                        innerTextField = innerTextField,
                        enabled = enabled,
                        singleLine = state.isSingleLine,
                        visualTransformation = effectiveTransformation,
                        interactionSource = interactionSource,
                        isError = state.hasError,
                        label = labelSlot,
                        placeholder = placeholderSlot,
                        leadingIcon = leadingSlot,
                        trailingIcon = trailingSlot,
                        prefix = prefixSlot,
                        suffix = suffixSlot,
                        shape = fieldShape,
                        colors = fieldColors,
                        contentPadding = contentPadding
                            ?: if (labelText != null) TextFieldDefaults.contentPaddingWithLabel() else TextFieldDefaults.contentPaddingWithoutLabel(),
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
                        singleLine = state.isSingleLine,
                        visualTransformation = effectiveTransformation,
                        interactionSource = interactionSource,
                        isError = state.hasError,
                        label = labelSlot,
                        placeholder = placeholderSlot,
                        leadingIcon = leadingSlot,
                        trailingIcon = trailingSlot,
                        prefix = prefixSlot,
                        suffix = suffixSlot,
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

        if (state.subtitleRes != null && !state.hasError) {
            Text(
                stringResource(state.subtitleRes),
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = Dimens.quarterGrid),
            )
        }

        if (state.hasError && (errorText != null || state.showMaxChar)) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = Dimens.halfGrid)) {
                if (errorText != null) {
                    Text(
                        text = errorText,
                        color = colorScheme.error,
                    )
                }
                if (state.showMaxChar) {
                    Text(
                        text = "${state.value.length} / ${state.maxChar}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

/** The typed value after type filtering, the dot rule, upper-casing and the length limit; null when it is too long. */
internal fun FormInputTextFieldState.sanitize(new: TextFieldValue): TextFieldValue? {
    val isNumeric = type == FormInputType.NUMBER
    val isPhone = type == FormInputType.PHONE
    var filtered = when {
        isNumeric -> new.text.filter { it.isDigit() || it == '.' }
        isPhone -> new.text.filter { it.isDigit() || it == '+' }
        else -> new.text
    }
    if (isNumeric && filtered.count { it == '.' } > 1) {
        val firstDotIndex = filtered.indexOf('.')
        filtered = filtered.substring(0, firstDotIndex + 1) + filtered.substring(firstDotIndex + 1).replace(".", "")
    }
    if (filtered.length > maxChar) return null
    val formatted = if (autoCapitalize) filtered.uppercase() else filtered
    val length = formatted.length
    return new.copy(
        text = formatted,
        selection = TextRange(new.selection.start.coerceAtMost(length), new.selection.end.coerceAtMost(length)),
    )
}

private fun getKeyboardOptions(state: FormInputTextFieldState): KeyboardOptions {
    val keyboardType = when (state.type) {
        FormInputType.PHONE -> KeyboardType.Phone
        FormInputType.NUMBER -> KeyboardType.Number
        FormInputType.EMAIL -> KeyboardType.Email
        FormInputType.PASSWORD -> KeyboardType.Password
        FormInputType.URL -> KeyboardType.Uri
        else -> KeyboardType.Text
    }

    val capitalization = when {
        state.autoCapitalize -> KeyboardCapitalization.Characters
        state.type == FormInputType.TEXT -> KeyboardCapitalization.Words
        else -> KeyboardCapitalization.None
    }

    return KeyboardOptions(
        keyboardType = keyboardType,
        capitalization = capitalization,
    )
}

@Composable
private fun getTrailingIcon(
    state: FormInputTextFieldState,
    passwordVisible: Boolean,
    onPasswordToggle: () -> Unit,
): @Composable (() -> Unit)? {
    if (state.type != FormInputType.PASSWORD) return null

    return {
        IconButton(onClick = onPasswordToggle) {
            Icon(
                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = if (passwordVisible) formInputString(FormInputStrings::hidePassword, Res.string.hide_password) else formInputString(FormInputStrings::showPassword, Res.string.show_password),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
