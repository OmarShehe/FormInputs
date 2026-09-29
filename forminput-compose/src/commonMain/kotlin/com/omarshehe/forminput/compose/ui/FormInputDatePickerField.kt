package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.Modifier
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
import com.omarshehe.forminput.compose.ui.composables.pickerSupportingText
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.utils.DateUtils
import com.omarshehe.forminput.compose.ui.utils.utcYear
import com.omarshehe.forminput.compose.ui.utils.utcMillisToDate
import com.omarshehe.forminput.compose.ui.utils.todayUtcMidnightMillis
import com.omarshehe.forminput.compose.ui.utils.dateToUtcMillis
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.trimOutlinedLabelSpace
import com.omarshehe.forminput.compose.ui.utils.Symbols

@Composable
fun FormInputDatePickerField(
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
    DatePickerField(
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
private fun DatePickerField(
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
    // Keyed on the value: the picker state only reads its initial date once, so a value that arrives after the
    // first composition (an edit screen loading a saved date) would otherwise open on the old date.
    val datePickerState = key(value) {
        rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis ?: fallbackMillis,
            selectableDates = selectableDates,
        )
    }

    val hasLabelSpace = label != null

    Box(modifier = modifier) {
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
                            contentDescription = formInputString(FormInputStrings::selectDate, Res.string.select_date),
                            modifier = Modifier.size(Dimens.twoAndHalfGrid),
                        )
                    }
                },
                isError = hasError,
                supportingText = pickerSupportingText(hasError, error, supportingText),
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
                            contentDescription = formInputString(FormInputStrings::selectDate, Res.string.select_date),
                            modifier = Modifier.size(Dimens.twoAndHalfGrid),
                        )
                    }
                },
                isError = hasError,
                supportingText = pickerSupportingText(hasError, error, supportingText),
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
                    .fillMaxWidth()
                    .height(TextFieldDefaults.MinHeight)
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
                        datePickerState.selectedDateMillis?.let {
                            onValueChange(it.utcMillisToDate())
                        }
                        showDatePicker = false
                    }) {
                        Text(formInputString(FormInputStrings::ok, Res.string.ok))
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
    }
}
