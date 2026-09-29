package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import com.omarshehe.forminput.compose.ui.utils.DateUtils
import com.omarshehe.forminput.compose.ui.utils.utcYear
import com.omarshehe.forminput.compose.ui.utils.utcMillisToDate
import com.omarshehe.forminput.compose.ui.utils.nowMillis
import com.omarshehe.forminput.compose.ui.utils.dateToUtcMillis
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.Symbols

@Composable
fun FormInputDatePickerField(
    state: FormInputDateTimePickerState,
    onValueChange: (FormInputDateTimePickerState) -> Unit,
    modifier: Modifier = Modifier,
) {
    DatePickerField(
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
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
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
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var textValueState by remember { mutableStateOf(TextFieldValue(text = value)) }

    LaunchedEffect(value) {
        if (textValueState.text != value) {
            textValueState = textValueState.copy(text = value, selection = TextRange(value.length))
        }
    }

    val initialSelectedDateMillis = remember(value) {
        value.dateToUtcMillis()
    }

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
        initialSelectedDateMillis = initialSelectedDateMillis ?: fallbackMillis,
        selectableDates = selectableDates,
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
                        contentDescription = stringResource(Res.string.select_date),
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
                        datePickerState.selectedDateMillis?.let {
                            onValueChange(it.utcMillisToDate())
                        }
                        showDatePicker = false
                    }) {
                        Text(stringResource(Res.string.ok))
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
    }
}
