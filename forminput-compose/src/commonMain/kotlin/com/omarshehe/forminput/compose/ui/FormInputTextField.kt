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
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.Symbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputTextField(
    state: FormInputTextFieldState,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
    onValueChange: (FormInputTextFieldState) -> Unit = {},
) {
    val labelText = state.resolvedLabel()
    val placeholderText = state.resolvedPlaceholder()
    val errorText = state.resolvedError()
    var textValueState by remember { mutableStateOf(TextFieldValue(text = state.value)) }
    var passwordVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(state.value) {
        if (textValueState.text != state.value) {
            textValueState =
                textValueState.copy(text = state.value, selection = TextRange(state.value.length))
        }
    }

    val visualTransformation = if (state.type == FormInputType.PASSWORD && !passwordVisible) {
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
            value = textValueState,
            onValueChange = { newValueState ->
                val isNumeric = state.type == FormInputType.NUMBER
                val isPhone = state.type == FormInputType.PHONE
                var filteredText = when {
                    isNumeric -> newValueState.text.filter { it.isDigit() || it == '.' }
                    isPhone -> newValueState.text.filter { it.isDigit() || it == '+' }
                    else -> newValueState.text
                }

                if (isNumeric && filteredText.count { it == '.' } > 1) {
                    val firstDotIndex = filteredText.indexOf('.')
                    val beforeDot = filteredText.substring(0, firstDotIndex + 1)
                    val afterDot = filteredText.substring(firstDotIndex + 1).replace(".", "")
                    filteredText = beforeDot + afterDot
                }

                if (filteredText.length <= state.maxChar) {
                    val formattedValue =
                        if (state.autoCapitalize) filteredText.uppercase() else filteredText
                    textValueState = newValueState.copy(text = formattedValue)
                    onValueChange(state.copy(value = formattedValue))
                }
            },
            modifier = textModifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface),
            keyboardOptions = getKeyboardOptions(state),
            visualTransformation = visualTransformation,
            singleLine = state.isSingleLine,
            minLines = state.minLines,
            maxLines = state.maxLines,
            cursorBrush = SolidColor(colorScheme.primary),
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = textValueState.text,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = state.isSingleLine,
                    visualTransformation = visualTransformation,
                    interactionSource = interactionSource,
                    isError = state.hasError,
                    label = labelText?.let {
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
                    },
                    placeholder = placeholderText?.let { { Text(it) } },
                    leadingIcon = state.icon?.let {
                        { Icon(imageVector = it, contentDescription = null, tint = colorScheme.primary) }
                    },
                    trailingIcon = getTrailingIcon(state, passwordVisible) { passwordVisible = !passwordVisible },
                    prefix = state.prefixRes?.let { { Text(stringResource(it)) } },
                    suffix = state.suffixRes?.let { { Text(stringResource(it)) } },
                    colors = colors,
                    contentPadding = contentPadding,
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = state.hasError,
                            interactionSource = interactionSource,
                            colors = colors,
                            shape = shape,
                        )
                    },
                )
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
                contentDescription = stringResource(if (passwordVisible) Res.string.hide_password else Res.string.show_password),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
