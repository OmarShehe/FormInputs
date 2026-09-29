package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.omarshehe.forminput.compose.ui.utils.DateUtils
import com.omarshehe.forminput.compose.ui.utils.utcYear
import com.omarshehe.forminput.compose.ui.utils.toDateTimeParts
import com.omarshehe.forminput.compose.ui.utils.todayUtcMidnightMillis
import com.omarshehe.forminput.compose.ui.utils.nowMillis
import com.omarshehe.forminput.compose.ui.utils.formatUtcMillisPattern
import com.omarshehe.forminput.compose.ui.utils.formatDateTime
import com.omarshehe.forminput.compose.ui.utils.dateTimeUtcMillis
import com.omarshehe.forminput.compose.ui.utils.currentLocalHourMinute
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.Symbols

@Composable
fun FormInputDateTimePickerField(
    state: FormInputDateTimePickerState,
    onValueChange: (FormInputDateTimePickerState) -> Unit,
    modifier: Modifier = Modifier,
    displayFormat: String = DateUtils.DATE_TIME_DISPLAY_FORMAT,
) {
    DateTimePickerField(
        value = state.value,
        onValueChange = { newValue -> onValueChange(state.copy(value = newValue)) },
        labelRes = state.labelRes,
        placeholderRes = state.placeholderRes,
        isMandatory = state.isMandatory,
        hasError = state.hasError,
        errorRes = state.errorRes,
        isManualEditable = state.isManualEditable,
        minDateMillis = state.minDateMillis,
        maxDateMillis = state.maxDateMillis,
        displayFormat = displayFormat,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelRes: org.jetbrains.compose.resources.StringResource? = null,
    placeholderRes: org.jetbrains.compose.resources.StringResource? = null,
    isMandatory: Boolean = false,
    hasError: Boolean = false,
    errorRes: org.jetbrains.compose.resources.StringResource? = null,
    isManualEditable: Boolean = false,
    minDateMillis: Long? = null,
    maxDateMillis: Long? = null,
    displayFormat: String = DateUtils.DATE_TIME_DISPLAY_FORMAT,
) {
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
        val now = nowMillis()
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
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialParts?.dateUtcMillis ?: fallbackMillis,
        selectableDates = selectableDates,
    )
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.first,
        initialMinute = initialTime.second,
        is24Hour = true,
    )

    // OutlinedTextField(value, onValueChange, ...) reserves extra top space above its own border
    // (half the floated label's line height) to make room for the label to float there — space that
    // FormInputTextField's hand-rolled BasicTextField + DecorationBox never reserves. Left alone,
    // this field sits visibly lower than a text field in the same row. Cancel that reserved space out
    // so both field types line up.
    val minimizedLabelHalfHeight = if (labelRes != null) {
        with(LocalDensity.current) { MaterialTheme.typography.bodySmall.lineHeight.toDp() / 2 }
    } else {
        0.dp
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = textValueState,
            onValueChange = { newValueState ->
                textValueState = newValueState
                if (isManualEditable && newValueState.text != value) {
                    onValueChange(newValueState.text)
                }
            },
            readOnly = !isManualEditable,
            label = labelRes?.let {
                {
                    val label = stringResource(it)
                    Text(if (isMandatory) "$label${Symbols.MANDATORY_SYMBOL}" else label)
                }
            },
            placeholder = placeholderRes?.let { { Text(stringResource(it)) } },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        Icons.AutoMirrored.Filled.EventNote,
                        contentDescription = stringResource(Res.string.select_date_time),
                        modifier = Modifier.size(Dimens.twoAndHalfGrid),
                    )
                }
            },
            isError = hasError,
            supportingText = if (hasError && errorRes != null) {
                { Text(stringResource(errorRes)) }
            } else {
                null
            },
            modifier = Modifier.offset(y = -minimizedLabelHalfHeight).fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (hasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            ),
        )
        if (!isManualEditable) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
                    .pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true),
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                shape = MaterialTheme.shapes.large,
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        showTimePicker = true
                    }) {
                        Text(stringResource(Res.string.next))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(Res.string.cancel))
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            DatePickerDialog(
                onDismissRequest = { showTimePicker = false },
                shape = MaterialTheme.shapes.large,
                confirmButton = {
                    TextButton(onClick = {
                        val date = datePickerState.selectedDateMillis ?: todayUtcMidnightMillis()
                        onValueChange(formatDateTime(date, timePickerState.hour, timePickerState.minute))
                        showTimePicker = false
                    }) {
                        Text(stringResource(Res.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showTimePicker = false
                        showDatePicker = true
                    }) {
                        Text(stringResource(Res.string.back))
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
}
