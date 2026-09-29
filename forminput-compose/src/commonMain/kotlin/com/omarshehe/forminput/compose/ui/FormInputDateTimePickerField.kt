package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omarshehe.forminput.compose.ui.composables.PickerSupportingText
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.back
import com.omarshehe.forminput.compose.resources.cancel
import com.omarshehe.forminput.compose.resources.next
import com.omarshehe.forminput.compose.resources.ok
import com.omarshehe.forminput.compose.resources.select_date_time
import org.jetbrains.compose.resources.stringResource
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.composables.resolvedPlaceholder
import com.omarshehe.forminput.compose.ui.composables.resolvedLabel
import com.omarshehe.forminput.compose.ui.composables.resolvedError
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.utils.DateUtils
import com.omarshehe.forminput.compose.ui.utils.utcYear
import com.omarshehe.forminput.compose.ui.utils.toDateTimeParts
import com.omarshehe.forminput.compose.ui.utils.todayUtcMidnightMillis
import com.omarshehe.forminput.compose.ui.utils.formatUtcMillisPattern
import com.omarshehe.forminput.compose.ui.utils.formatDateTime
import com.omarshehe.forminput.compose.ui.utils.dateTimeUtcMillis
import com.omarshehe.forminput.compose.ui.utils.currentLocalHourMinute
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.trimOutlinedLabelSpace
import com.omarshehe.forminput.compose.ui.utils.Symbols

@Composable
fun FormInputDateTimePickerField(
    state: FormInputDateTimePickerState,
    onValueChange: (FormInputDateTimePickerState) -> Unit,
    modifier: Modifier = Modifier,
    displayFormat: String = DateUtils.DATE_TIME_DISPLAY_FORMAT,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
    enabled: Boolean = true,
    supportingText: String? = null,
    fieldModifier: Modifier = Modifier,
    dialogShape: Shape? = null,
    dialogContainerColor: Color? = null,
) {
    DateTimePickerField(
        value = state.value,
        onValueChange = { newValue -> onValueChange(state.copy(value = newValue)) },
        label = state.resolvedLabel(),
        placeholder = state.resolvedPlaceholder(),
        isMandatory = state.isMandatory,
        hasError = state.hasError,
        error = state.resolvedError(),
        isManualEditable = state.isManualEditable,
        minDateMillis = state.minDateMillis,
        maxDateMillis = state.maxDateMillis,
        displayFormat = displayFormat,
        shape = shape,
        style = style,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isMandatory: Boolean = false,
    hasError: Boolean = false,
    error: String? = null,
    isManualEditable: Boolean = false,
    minDateMillis: Long? = null,
    maxDateMillis: Long? = null,
    displayFormat: String = DateUtils.DATE_TIME_DISPLAY_FORMAT,
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
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    fun formatForDisplay(raw: String): String =
        raw.toDateTimeParts()?.let { formatUtcMillisPattern(dateTimeUtcMillis(it.dateUtcMillis, it.hour, it.minute), displayFormat) } ?: raw

    var textValueState by remember { mutableStateOf(TextFieldValue(text = formatForDisplay(value))) }

    LaunchedEffect(value) {
        val displayed = formatForDisplay(value)
        if (textValueState.text != displayed) {
            textValueState = textValueState.copy(text = displayed, selection = TextRange(displayed.length))
        }
    }

    val initialParts = remember(value) { value.toDateTimeParts() }
    val initialTime = remember(value) { initialParts?.let { it.hour to it.minute } ?: currentLocalHourMinute() }

    val fallbackMillis = remember(minDateMillis, maxDateMillis) {
        val now = todayUtcMidnightMillis()
        when {
            maxDateMillis != null && now > maxDateMillis -> maxDateMillis
            minDateMillis != null && now < minDateMillis -> minDateMillis
            else -> now
        }
    }
    val selectableDates = remember(minDateMillis, maxDateMillis) {
        if (minDateMillis != null || maxDateMillis != null) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val afterMin = minDateMillis == null || utcTimeMillis >= minDateMillis
                    val beforeMax = maxDateMillis == null || utcTimeMillis <= maxDateMillis
                    return afterMin && beforeMax
                }

                override fun isSelectableYear(year: Int): Boolean {
                    val minYear = minDateMillis?.utcYear()
                    val maxYear = maxDateMillis?.utcYear()
                    return (minYear == null || year >= minYear) && (maxYear == null || year <= maxYear)
                }
            }
        } else {
            DatePickerDefaults.AllDates
        }
    }
    // Keyed on the value so a value that arrives late is shown when the dialogs open.
    val datePickerState = key(value) {
        rememberDatePickerState(
            initialSelectedDateMillis = initialParts?.dateUtcMillis ?: fallbackMillis,
            selectableDates = selectableDates,
        )
    }
    val timePickerState = key(value) {
        rememberTimePickerState(
            initialHour = initialTime.first,
            initialMinute = initialTime.second,
            is24Hour = true,
        )
    }

    val hasLabelSpace = label != null

    Column(modifier = modifier) {
        Box {
            if (filled) {
                TextField(
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
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.AutoMirrored.Filled.EventNote,
                                contentDescription = formInputString(FormInputStrings::selectDateTime, Res.string.select_date_time),
                                modifier = Modifier.size(Dimens.twoAndHalfGrid),
                            )
                        }
                    },
                    isError = hasError,
                    modifier = fieldModifier.fillMaxWidth(),
                    shape = fieldShape,
                    colors = colors ?: TextFieldDefaults.colors(),
                )
            } else {
                OutlinedTextField(
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
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.AutoMirrored.Filled.EventNote,
                                contentDescription = formInputString(FormInputStrings::selectDateTime, Res.string.select_date_time),
                                modifier = Modifier.size(Dimens.twoAndHalfGrid),
                            )
                        }
                    },
                    isError = hasError,
                    modifier = fieldModifier.trimOutlinedLabelSpace(hasLabelSpace).fillMaxWidth(),
                    shape = fieldShape,
                    colors = colors ?: OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    ),
                )
            }
            if (!isManualEditable && enabled) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(fieldShape)
                        .clickable { showDatePicker = true }
                        .pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true),
                )
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    shape = dialogShape ?: MaterialTheme.shapes.large,
                    colors = dialogContainerColor?.let { DatePickerDefaults.colors(containerColor = it) } ?: DatePickerDefaults.colors(),
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            showTimePicker = true
                        }) {
                            Text(formInputString(FormInputStrings::next, Res.string.next))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(formInputString(FormInputStrings::cancel, Res.string.cancel))
                        }
                    },
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (showTimePicker) {
                DatePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    shape = dialogShape ?: MaterialTheme.shapes.large,
                    colors = dialogContainerColor?.let { DatePickerDefaults.colors(containerColor = it) } ?: DatePickerDefaults.colors(),
                    confirmButton = {
                        TextButton(onClick = {
                            val date = datePickerState.selectedDateMillis ?: todayUtcMidnightMillis()
                            onValueChange(formatDateTime(date, timePickerState.hour, timePickerState.minute))
                            showTimePicker = false
                        }) {
                            Text(formInputString(FormInputStrings::ok, Res.string.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showTimePicker = false
                            showDatePicker = true
                        }) {
                            Text(formInputString(FormInputStrings::back, Res.string.back))
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
