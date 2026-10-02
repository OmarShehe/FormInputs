package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.DropDownOptionModel
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputPasswordState
import com.omarshehe.forminput.compose.ui.model.FormInputPriceState
import com.omarshehe.forminput.compose.ui.model.FormInputState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import kotlin.test.Test

/** A form can be drawn from a list of states: `FormInputField` must know every state type that has an input. */
@OptIn(ExperimentalTestApi::class)
class FormInputFieldDispatchTest {
    @Test
    fun aPasswordStateIsDrawnAsAPasswordField() = runComposeUiTest {
        setContent { FormInputField(state = FormInputPasswordState(id = "p", label = "Secret"), onValueChange = {}) }
        onNodeWithText("Secret").assertExists()
        onNodeWithText("Your password needs to:").assertExists()
    }

    @Test
    fun aPriceStateIsDrawnAsAPriceField() = runComposeUiTest {
        setContent {
            FormInputField(state = FormInputPriceState(id = "x", currency = "TSH", label = "Amount due"), onValueChange = {})
        }
        onNodeWithText("Amount due").assertExists()
    }

    @Test
    fun aWholeFormCanBeDrawnFromAListOfStates() = runComposeUiTest {
        var fields by mutableStateOf<List<FormInputState>>(
            listOf(
                FormInputTextFieldState(id = "name", type = FormInputType.TEXT, value = "", label = "Full name"),
                FormInputDropDownState(id = "region", label = "Region", options = listOf(DropDownOptionModel("1", "Dodoma"))),
                FormInputDateTimePickerState(
                    id = "when", labelRes = null, placeholderRes = null, type = FormInputType.DATE_PICKER, label = "Start date",
                ),
                FormInputPasswordState(id = "pw", label = "Password"),
                FormInputPriceState(id = "fee", currency = "USD", label = "Fee"),
            ),
        )
        setContent {
            androidx.compose.foundation.layout.Column {
                fields.forEach { state ->
                    FormInputField(state = state, onValueChange = { new -> fields = fields.map { if (it.id == new.id) new else it } })
                }
            }
        }
        listOf("Full name", "Region", "Start date", "Password", "Fee").forEach { onNodeWithText(it).assertExists() }
    }
}
