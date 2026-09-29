package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omarshehe.forminput.compose.ui.composables.FormInputBoxField
import com.omarshehe.forminput.compose.ui.composables.PickerSupportingText
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.resources.*
import com.omarshehe.forminput.compose.resources.Res
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.composables.resolvedPlaceholder
import com.omarshehe.forminput.compose.ui.composables.resolvedLabel
import com.omarshehe.forminput.compose.ui.composables.resolvedError
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.utils.DateUtils
import com.omarshehe.forminput.compose.ui.utils.toHourMinute
import com.omarshehe.forminput.compose.ui.utils.formatHourMinute
import com.omarshehe.forminput.compose.ui.utils.currentLocalHourMinute
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.Symbols

@Composable
fun FormInputTimePickerField(
    state: FormInputDateTimePickerState,
    onValueChange: (FormInputDateTimePickerState) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
    enabled: Boolean = true,
    supportingText: String? = null,
    fieldModifier: Modifier = Modifier,
    dialogShape: Shape? = null,
    dialogContainerColor: Color? = null,
) {
    TimePickerField(
        value = state.value,
        onValueChange = { newValue -> onValueChange(state.copy(value = newValue)) },
        label = state.resolvedLabel(),
        placeholder = state.resolvedPlaceholder(),
        isMandatory = state.isMandatory,
        hasError = state.hasError,
        error = state.resolvedError(),
        isManualEditable = state.isManualEditable,
        modifier = modifier,
        shape = shape,
        style = style,
        colors = colors,
        enabled = enabled,
        supportingText = supportingText,
        fieldModifier = fieldModifier,
        dialogShape = dialogShape,
        dialogContainerColor = dialogContainerColor,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isMandatory: Boolean = false,
    hasError: Boolean = false,
    error: String? = null,
    isManualEditable: Boolean = false,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
    enabled: Boolean = true,
    supportingText: String? = null,
    fieldModifier: Modifier = Modifier,
    dialogShape: Shape? = null,
    dialogContainerColor: Color? = null,
) {
    val defaults = LocalFormInputDefaults.current
    val filled = (style ?: defaults.style) == FormInputFieldStyle.FILLED
    val fieldShape = shape ?: defaults.shape ?: if (filled) TextFieldDefaults.shape else OutlinedTextFieldDefaults.shape
    var showTimePicker by remember { mutableStateOf(false) }
    var textValueState by remember { mutableStateOf(TextFieldValue(text = value)) }

    LaunchedEffect(value) {
        if (textValueState.text != value) {
            textValueState = textValueState.copy(text = value, selection = TextRange(value.length))
        }
    }

    val initialTime = remember(value) { value.toHourMinute() ?: currentLocalHourMinute() }

    // Keyed on the value so a value that arrives late is shown when the dialog opens.
    val timePickerState = key(value) {
        rememberTimePickerState(
            initialHour = initialTime.first,
            initialMinute = initialTime.second,
            is24Hour = true,
        )
    }


    Column(modifier = modifier) {
        Box {
            FormInputBoxField(
                filled = filled,
                focusable = isManualEditable,
                value = textValueState,
                onValueChange = { newValueState ->
                    textValueState = newValueState
                    if (isManualEditable && newValueState.text != value) {
                        onValueChange(newValueState.text)
                    }
                },
                readOnly = !isManualEditable,
                enabled = enabled,
                label = label?.let { { Text(if (isMandatory) "$it${Symbols.MANDATORY_SYMBOL}" else it) } },
                placeholder = placeholder?.let { { Text(it) } },
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = formInputString(FormInputStrings::selectTime, Res.string.select_time),
                            modifier = Modifier.size(Dimens.twoAndHalfGrid),
                        )
                    }
                },
                isError = hasError,
                modifier = fieldModifier.fillMaxWidth(),
                shape = fieldShape,
                colors = colors ?: if (filled) {
                TextFieldDefaults.colors()
            } else {
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                )
            },
            )
        
            if (!isManualEditable && enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(fieldShape)
                        .clickable { showTimePicker = true }
                        .pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true),
                )
            }

            if (showTimePicker) {
                DatePickerDialog( // Using DatePickerDialog container for consistency in style
                    onDismissRequest = { showTimePicker = false },
                    shape = dialogShape ?: MaterialTheme.shapes.large,
                    colors = dialogContainerColor?.let { DatePickerDefaults.colors(containerColor = it) } ?: DatePickerDefaults.colors(),
                    confirmButton = {
                        TextButton(onClick = {
                            onValueChange(formatHourMinute(timePickerState.hour, timePickerState.minute))
                            showTimePicker = false
                        }) {
                            Text(formInputString(FormInputStrings::ok, Res.string.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(formInputString(FormInputStrings::cancel, Res.string.cancel))
                        }
                    },
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        TimePicker(
                            state = timePickerState,
                            modifier = Modifier.padding(top = Dimens.twoGrid),
                        )
                    }
                }
            }
    
        }
        PickerSupportingText(hasError, error, supportingText)
    }
}
