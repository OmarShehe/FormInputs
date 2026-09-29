package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.PopupProperties
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputResultState
import com.omarshehe.forminput.compose.ui.model.FormInputType.DROP_DOWN
import com.omarshehe.forminput.compose.ui.model.WhenError
import com.omarshehe.forminput.compose.ui.model.isError
import com.omarshehe.forminput.compose.ui.utils.formInputModifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInputDropDownOption(
    modifier: Modifier = Modifier,
    fieldModifier: Modifier = Modifier,
    formInputData: FormInputDropDownState,
    isSearchEnable: Boolean = false,
    enabled: Boolean = true,
    supportingText: String? = null,
    onSelected: (FormInputDropDownState) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val resultState by remember { mutableStateOf<FormInputResultState>(FormInputResultState.Idle(DROP_DOWN)) }
    // A menu left open when the field is disabled must not pop back open when it is enabled again.
    LaunchedEffect(enabled) { if (!enabled) expanded = false }
    val label = formInputData.labelValue.asText()
    // Keyed on the selected text so an external change (for example a cascade clearing this field) is shown.
    var textValueState by remember(formInputData.textValue) { mutableStateOf(TextFieldValue(text = formInputData.textValue)) }

    val filteringOptions = formInputData.options.takeIf { isSearchEnable && textValueState.text.isNotEmpty() }
        ?.filter { it.textValue.contains(textValueState.text, ignoreCase = true) }
        ?: formInputData.options

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded && enabled,
        onExpandedChange = { expanded = enabled && !expanded },
    ) {
        TextField(
            modifier = fieldModifier
                .menuAnchor(if (isSearchEnable) ExposedDropdownMenuAnchorType.PrimaryEditable else ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = enabled)
                .fillMaxWidth(),
            isError = resultState.isError,
            enabled = enabled,
            supportingText = supportingText?.let { { Text(it) } },
            readOnly = isSearchEnable.not(),
            singleLine = true,
            value = textValueState,
            onValueChange = { newValueState ->
                textValueState = newValueState
                if (!expanded) expanded = true
            },
            label = { Text(label) },
            placeholder = { Text(formInputData.placeholderValue.asText()) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && enabled) },
            colors = TextFieldDefaults.colors(),
        )

        if (filteringOptions.isNotEmpty()) {
            DropdownMenu(
                modifier = Modifier.exposedDropdownSize(true),
                properties = PopupProperties(focusable = false),
                expanded = expanded && enabled,
                onDismissRequest = { if (!isSearchEnable) expanded = false },
            ) {
                filteringOptions.forEach { selectionOption ->
                    val textValue = selectionOption.textValue
                    DropdownMenuItem(
                        text = { Text(textValue) },
                        onClick = {
                            textValueState = textValueState.copy(textValue, TextRange(textValue.length))
                            onSelected(formInputData.copy(value = selectionOption, hasError = false))
                            expanded = false
                        }
                    )
                }
            }
        }
    }
    resultState.WhenError {
        val error = "$label ${errorTextValue.asText()}"
        TextContent(textValue = error, color = MaterialTheme.colorScheme.error)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun FormInputDropDownOptionPreview() {
    val data = FormInputDropDownState(
        id = "Language",
        labelValue = "Language",
        placeholderValue = "Please select your language",
        type = DROP_DOWN,
        isMandatory = true
    )
    FormInputDropDownOption(
        modifier = formInputModifier(),
        formInputData = data
    ) {
    }
}
