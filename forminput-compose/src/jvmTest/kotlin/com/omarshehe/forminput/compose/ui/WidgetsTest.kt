package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.DropDownOptionModel
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class WidgetsTest {
    @Test
    fun textFieldReportsTypedValue() = runComposeUiTest {
        var state by mutableStateOf(
            FormInputTextFieldState(value = "", id = "n", labelValue = "Name", placeholderValue = "Your name", type = FormInputType.TEXT)
        )
        setContent { FormInputTextField(formInputData = state, onValueChange = { state = it }) }
        onNodeWithText("Name").performTextInput("Amina")
        assertEquals("Amina", state.value)
    }

    @Test
    fun dropDownSelectsOption() = runComposeUiTest {
        var state by mutableStateOf(
            FormInputDropDownState(
                options = listOf(DropDownOptionModel("1", "Dodoma"), DropDownOptionModel("2", "Arusha")),
                id = "r", labelValue = "Region", placeholderValue = "Pick", type = FormInputType.DROP_DOWN,
            )
        )
        setContent { FormInputDropDownOption(formInputData = state, onSelected = { state = it }) }
        onNodeWithText("Region").performClick()
        onNodeWithText("Arusha").performClick()
        assertEquals("2", state.value.id)
    }
}
