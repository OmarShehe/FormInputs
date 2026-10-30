package com.omarshehe.forminput.compose.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.PaddingValues
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
import com.omarshehe.forminput.compose.ui.utils.utcYear
import com.omarshehe.forminput.compose.ui.utils.utcMillisToDate
import com.omarshehe.forminput.compose.ui.utils.todayUtcMidnightMillis
import com.omarshehe.forminput.compose.ui.utils.dateToUtcMillis
import com.omarshehe.forminput.compose.ui.utils.formatDisplayDate
import com.omarshehe.forminput.compose.ui.utils.parseDisplayDate
import com.omarshehe.forminput.compose.ui.utils.Dimens
import com.omarshehe.forminput.compose.ui.utils.Symbols

/**
 * A date field that opens a date dialog; with [FormInputDateTimePickerState.isManualEditable] the user can also type the date.
 * [style], [shape] and [colors] fall back to the form-wide defaults from [FormInputTheme]. [textStyle] and [contentPadding] style
 * the field, [fieldModifier] reaches the field itself, and [dialogShape] and [dialogContainerColor] style the dialog.
 *
 * The date is shown in [displayFormat], a `SimpleDateFormat`-style pattern (month names follow the device language); the value in
 * the state stays `yyyy-MM-dd`. A manually typed date is read with [displayFormat] and stored once it is a valid date in that
 * pattern; clearing the text clears the value.
 */
@Composable
public fun FormInputDatePickerField(
    state: FormInputDateTimePickerState,
    onValueChange: (FormInputDateTimePickerState) -> Unit,
    modifier: Modifier = Modifier,
    displayFormat: String = DateUtils.DATE_FORMAT,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
    enabled: Boolean = true,
    supportingText: String? = null,
    fieldModifier: Modifier = Modifier,
    dialogShape: Shape? = null,
    dialogContainerColor: Color? = null,
    textStyle: TextStyle? = null,
    contentPadding: PaddingValues? = null,
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
        displayFormat = displayFormat,
        modifier = modifier,
        shape = shape,
        style = style,
        colors = colors,
        enabled = enabled,
        supportingText = supportingText,
        fieldModifier = fieldModifier,
        dialogShape = dialogShape,
        dialogContainerColor = dialogContainerColor,
        textStyle = textStyle,
        contentPadding = contentPadding,
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
    displayFormat: String = DateUtils.DATE_FORMAT,
    shape: Shape? = null,
    style: FormInputFieldStyle? = null,
    colors: TextFieldColors? = null,
    enabled: Boolean = true,
    supportingText: String? = null,
    fieldModifier: Modifier = Modifier,
    dialogShape: Shape? = null,
    dialogContainerColor: Color? = null,
    textStyle: TextStyle? = null,
    contentPadding: PaddingValues? = null,
) {
    val defaults = LocalFormInputDefaults.current
    val filled = (style ?: defaults.style) == FormInputFieldStyle.FILLED
    val fieldShape = shape ?: defaults.shape ?: if (filled) TextFieldDefaults.shape else OutlinedTextFieldDefaults.shape
    var showDatePicker by remember { mutableStateOf(false) }
    var textValueState by remember { mutableStateOf(TextFieldValue(text = formatDisplayDate(value, displayFormat))) }

    // Keyed on the format too, so switching the format while the value stays the same redraws the text. Half-typed text is
    // left alone: the value does not change until it parses, so this does not run for it.
    LaunchedEffect(value, displayFormat) {
        val displayed = formatDisplayDate(value, displayFormat)
        if (textValueState.text != displayed) {
            textValueState = textValueState.copy(text = displayed, selection = TextRange(displayed.length))
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
    // Keyed on the value and the fallback: the picker state only reads its initial date once, so a value that arrives
    // after the first composition (an edit screen loading a saved date), or a minimum that moves while the value is
    // empty (an end date following its start date), would otherwise open on the old date.
    val datePickerState = key(value, fallbackMillis) {
        rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis ?: fallbackMillis,
            selectableDates = selectableDates,
        )
    }


    Column(modifier = modifier) {
        Box {
            FormInputBoxField(
                textStyle = textStyle,
                contentPadding = contentPadding,
                filled = filled,
                focusable = isManualEditable,
                value = textValueState,
                onValueChange = { newValueState ->
                    textValueState = newValueState
                    if (isManualEditable) {
                        val typed = newValueState.text
                        val stored = if (typed.isEmpty()) "" else parseDisplayDate(typed, displayFormat)
                        if (stored != null && stored != value) onValueChange(stored)
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
                        .clickable { showDatePicker = true }
                        .pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true),
                )
            }

            if (showDatePicker) {
                NotSelectableLayer {
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
        PickerSupportingText(hasError, error, supportingText)
    }
}
