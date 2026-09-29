package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.composables.FormInputLabel
import com.omarshehe.forminput.compose.ui.composables.PickerSupportingText
import com.omarshehe.forminput.compose.ui.composables.resolvedError
import com.omarshehe.forminput.compose.ui.composables.resolvedLabel
import com.omarshehe.forminput.compose.ui.composables.resolvedPlaceholder
import com.omarshehe.forminput.compose.ui.composables.TextContent
import com.omarshehe.forminput.compose.ui.model.DropDownOptionModel
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.trimOutlinedLabelSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputDropDownField(
    modifier: Modifier = Modifier,
    state: FormInputDropDownState,
    colors: TextFieldColors? = null,
    shape: Shape? = null,
    enabled: Boolean = true,
    supportingText: String? = null,
    fieldModifier: Modifier = Modifier,
    style: FormInputFieldStyle? = null,
    textStyle: TextStyle? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    menuShape: Shape? = null,
    menuContainerColor: Color? = null,
    maxMenuHeight: Dp = 240.dp,
    itemContent: (@Composable (DropDownOptionModel) -> Unit)? = null,
    onSelected: (FormInputDropDownState) -> Unit,
) {
    val defaults = LocalFormInputDefaults.current
    val filled = (style ?: defaults.style) == FormInputFieldStyle.FILLED
    val fieldColors = colors ?: if (filled) TextFieldDefaults.colors() else OutlinedTextFieldDefaults.colors()
    val fieldShape = shape ?: defaults.shape ?: if (filled) TextFieldDefaults.shape else OutlinedTextFieldDefaults.shape
    var expanded by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val currentText = state.valueTextRes?.let { stringResource(it) } ?: state.valueText
    var textValueState by remember { mutableStateOf(TextFieldValue(text = currentText)) }
    val allowFreeText = state.allowFreeText
    val labelText = state.resolvedLabel()
    val placeholderText = state.resolvedPlaceholder()
    val errorText = state.resolvedError()

    // A menu left open when the field is disabled must not pop back open when it is enabled again.
    LaunchedEffect(enabled) { if (!enabled) expanded = false }
    val editable = state.isSearchEnable || allowFreeText

    LaunchedEffect(currentText) {
        if (textValueState.text != currentText) {
            textValueState = textValueState.copy(text = currentText, selection = TextRange(currentText.length))
        }
    }

    val filteringOptions = if (editable && textValueState.text.isNotEmpty() && (allowFreeText || textValueState.text != state.valueText)) {
        state.options.filter {
            (it.textRes?.let { res -> stringResource(res) } ?: it.text).contains(textValueState.text, ignoreCase = true)
        }
    } else {
        state.options
    }

    val hasLabelSpace = labelText != null && !filled

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded && enabled,
            onExpandedChange = { expanded = enabled && it },
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (filled) {
                    TextField(
                        modifier = fieldModifier
                            .trimOutlinedLabelSpace(hasLabelSpace)
                            .menuAnchor(
                                if (editable) ExposedDropdownMenuAnchorType.PrimaryEditable else ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = enabled,
                            )
                            .fillMaxWidth(),
                        enabled = enabled,
                        isError = state.hasError,
                        readOnly = !editable,
                        singleLine = true,
                        value = textValueState,
                        onValueChange = { newValueState ->
                            textValueState = newValueState
                            if (allowFreeText) {
                                // Only auto-open while there's text to filter against — otherwise
                                // backspacing to empty falls back to the unfiltered full option list,
                                // reproducing the same "suggestions flood the screen" problem. Scoped to
                                // allowFreeText only — isSearchEnable-only dropdowns (desktop date pickers)
                                // rely on reopening with the full unfiltered list on any keystroke, including
                                // backspace-to-empty.
                                expanded = newValueState.text.isNotEmpty()
                                onSelected(state.copy(value = DropDownOptionModel(text = newValueState.text), hasError = false))
                            } else if (editable) {
                                expanded = true
                            }
                        },
                        label = labelText?.let {
                            { FormInputLabel(state) }
                        },
                        placeholder = placeholderText?.let { { Text(it) } },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && enabled) },
                        colors = fieldColors,
                        shape = fieldShape,
                        leadingIcon = leadingIcon,
                        textStyle = textStyle ?: LocalTextStyle.current,
                    )
                } else {
                    OutlinedTextField(
                        modifier = fieldModifier
                            .trimOutlinedLabelSpace(hasLabelSpace)
                            .menuAnchor(
                                if (editable) ExposedDropdownMenuAnchorType.PrimaryEditable else ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = enabled,
                            )
                            .fillMaxWidth(),
                        enabled = enabled,
                        isError = state.hasError,
                        readOnly = !editable,
                        singleLine = true,
                        value = textValueState,
                        onValueChange = { newValueState ->
                            textValueState = newValueState
                            if (allowFreeText) {
                                // Only auto-open while there's text to filter against — otherwise
                                // backspacing to empty falls back to the unfiltered full option list,
                                // reproducing the same "suggestions flood the screen" problem. Scoped to
                                // allowFreeText only — isSearchEnable-only dropdowns (desktop date pickers)
                                // rely on reopening with the full unfiltered list on any keystroke, including
                                // backspace-to-empty.
                                expanded = newValueState.text.isNotEmpty()
                                onSelected(state.copy(value = DropDownOptionModel(text = newValueState.text), hasError = false))
                            } else if (editable) {
                                expanded = true
                            }
                        },
                        label = labelText?.let {
                            { FormInputLabel(state) }
                        },
                        placeholder = placeholderText?.let { { Text(it) } },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && enabled) },
                        colors = fieldColors,
                        shape = fieldShape,
                        leadingIcon = leadingIcon,
                        textStyle = textStyle ?: LocalTextStyle.current,
                    )
                }
                if (!editable && enabled) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(fieldShape)
                            .clickable { expanded = !expanded }
                            .pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true),
                    )
                }
            }

            if (filteringOptions.isNotEmpty()) {
                DropdownMenu(
                    // Capped so a broad "contains" match (e.g. a single common typed character
                    // matching most options) scrolls internally instead of the popup swelling to
                    // cover most of the screen.
                    modifier = Modifier.exposedDropdownSize(true).heightIn(max = maxMenuHeight),
                    shape = menuShape ?: MenuDefaults.shape,
                    containerColor = menuContainerColor ?: MenuDefaults.containerColor,
                    expanded = expanded && enabled,
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = false),
                ) {
                    filteringOptions.forEach { selectionOption ->
                        val textValue = selectionOption.textRes?.let { stringResource(it) } ?: selectionOption.text
                        DropdownMenuItem(
                            text = { itemContent?.invoke(selectionOption) ?: Text(textValue) },
                            onClick = {
                                textValueState = textValueState.copy(textValue, TextRange(textValue.length))
                                onSelected(state.copy(value = selectionOption, hasError = false))
                                expanded = false
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
        PickerSupportingText(hasError = false, error = null, hint = supportingText)
        if (state.hasError) {
            // A runtime error is shown as written, like the text field; a resource error keeps the "<label> <error>" form.
            val error = if (state.error != null) errorText.orEmpty() else "${labelText.orEmpty()} ${errorText.orEmpty()}"
            TextContent(textValue = error, modifier = Modifier.padding(top = Dimens.halfGrid), color = colorScheme.error, setPadding = false)
        }
    }
}
