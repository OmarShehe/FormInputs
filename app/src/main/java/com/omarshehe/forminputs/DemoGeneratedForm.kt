package com.omarshehe.forminputs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.omarshehe.forminput.compose.ui.FormInputField
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputPasswordState
import com.omarshehe.forminput.compose.ui.model.FormInputPriceState
import com.omarshehe.forminput.compose.ui.model.FormInputState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType

/** One list of states; each is drawn by `FormInputField`, and the state it hands back replaces the one with the same id. */
@Composable
internal fun GeneratedFormSection(look: DemoLook) = DemoSection("Generated from a list of states") {
    var fields by remember { mutableStateOf(generatedFormStates()) }
    fields.forEach { state ->
        FormInputField(
            state = state,
            onValueChange = { updated -> fields = fields.replacing(updated) },
            modifier = look.fieldModifier,
        )
    }
}

private fun generatedFormStates(): List<FormInputState> = listOf(
    FormInputTextFieldState(id = "generated_name", type = FormInputType.TEXT, value = "", label = "Name (generated)", isMandatory = true),
    FormInputDropDownState(id = "generated_region", label = "Region (generated)", options = regions),
    FormInputDateTimePickerState(
        id = "generated_start", labelRes = null, placeholderRes = null, type = FormInputType.DATE_PICKER, label = "Start date (generated)",
    ),
    FormInputPasswordState(id = "generated_password", label = "Password (generated)"),
    FormInputPriceState(id = "generated_fee", currency = "TSH", currencies = listOf("TSH", "USD"), label = "Fee (generated)"),
)

private fun List<FormInputState>.replacing(updated: FormInputState): List<FormInputState> =
    map { if (it.id == updated.id) updated else it }
