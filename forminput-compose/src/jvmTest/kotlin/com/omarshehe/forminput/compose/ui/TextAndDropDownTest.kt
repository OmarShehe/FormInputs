package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.omarshehe.forminput.compose.ui.model.DropDownOptionModel
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import com.omarshehe.forminput.compose.ui.utils.formInputShape
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class TextAndDropDownTest {
    private fun text(
        type: FormInputType = FormInputType.TEXT,
        value: String = "",
        maxChar: Int = Int.MAX_VALUE,
        autoCapitalize: Boolean = false,
        hasError: Boolean = false,
        error: String? = null,
    ) = FormInputTextFieldState(
        id = "f", label = "Field", placeholder = "Type here", type = type, value = value,
        maxChar = maxChar, autoCapitalize = autoCapitalize, hasError = hasError, error = error,
    )

    private fun regions(selected: DropDownOptionModel = DropDownOptionModel(), search: Boolean = false, free: Boolean = false) =
        FormInputDropDownState(
            id = "r", label = "Region", placeholder = "Pick", value = selected,
            options = listOf(DropDownOptionModel("1", "Dodoma"), DropDownOptionModel("2", "Arusha"), DropDownOptionModel("3", "Arta")),
            isSearchEnable = search, allowFreeText = free,
        )

    private fun runText(initial: FormInputTextFieldState, block: androidx.compose.ui.test.ComposeUiTest.(state: () -> FormInputTextFieldState) -> Unit) =
        runComposeUiTest {
            var state by mutableStateOf(initial)
            setContent { FormInputTextField(state = state, onValueChange = { state = it }) }
            block { state }
        }

    @Test
    fun prefixAndSuffixAreShown() = runText(text().copy(prefix = "TZS", suffix = "kg", value = "5")) {
        onNodeWithText("TZS").assertExists()
        onNodeWithText("kg").assertExists()
    }

    @Test
    fun runtimeLabelIsShown() = runText(text()) {
        onNodeWithText("Field").assertExists()
    }

    @Test
    fun numberFieldKeepsDigitsAndOneDot() = runText(text(FormInputType.NUMBER)) { state ->
        onNodeWithText("Field").performTextInput("1a2.3.4")
        assertEquals("12.34", state().value)
    }

    @Test
    fun phoneFieldKeepsDigitsAndPlus() = runText(text(FormInputType.PHONE)) { state ->
        onNodeWithText("Field").performTextInput("+255 7(12)")
        assertEquals("+255712", state().value)
    }

    @Test
    fun autoCapitalizeUppercases() = runText(text(autoCapitalize = true)) { state ->
        onNodeWithText("Field").performTextInput("abc")
        assertEquals("ABC", state().value)
    }

    @Test
    fun inputBeyondMaxCharIsRejected() = runText(text(maxChar = 3)) { state ->
        onNodeWithText("Field").performTextInput("abcdef")
        assertEquals("", state().value)
    }

    @Test
    fun errorTextIsShownWhenInError() = runText(text(hasError = true, error = "Required")) {
        onNodeWithText("Required").assertExists()
    }

    @Test
    fun passwordIsMaskedUntilToggled() = runText(text(FormInputType.PASSWORD, value = "secret")) {
        onNodeWithText("\u2022\u2022\u2022\u2022\u2022\u2022").assertExists()
        onNodeWithContentDescription("Show password").performClick()
        onNodeWithText("secret").assertExists()
    }

    @Test
    fun dropDownSelectsOption() = runComposeUiTest {
        var state by mutableStateOf(regions())
        setContent { FormInputDropDownField(state = state, onSelected = { state = it }) }
        onNodeWithText("Region").performClick()
        onNodeWithText("Arusha").performClick()
        assertEquals("2", state.value.id)
    }

    @Test
    fun searchableDropDownFiltersWhileTyping() = runComposeUiTest {
        var state by mutableStateOf(regions(search = true))
        setContent { FormInputDropDownField(state = state, onSelected = { state = it }) }
        onNodeWithText("Region").performTextInput("Aru")
        onNodeWithText("Arusha").assertExists()
        onNodeWithText("Dodoma").assertDoesNotExist()
    }

    @Test
    fun freeTextDropDownReportsEveryKeystroke() = runComposeUiTest {
        var state by mutableStateOf(regions(free = true))
        setContent { FormInputDropDownField(state = state, onSelected = { state = it }) }
        onNodeWithText("Region").performTextInput("Zan")
        assertEquals("Zan", state.value.text)
    }

    @Test
    fun clearedSelectionIsShown() = runComposeUiTest {
        var state by mutableStateOf(regions(DropDownOptionModel("2", "Arusha")))
        setContent { FormInputDropDownField(state = state, onSelected = { state = it }) }
        onNodeWithText("Arusha").assertExists()
        state = regions()
        waitForIdle()
        onNodeWithText("Arusha").assertDoesNotExist()
    }

    @Test
    fun disabledDropDownDoesNotOpenAndShowsSupportingText() = runComposeUiTest {
        setContent { FormInputDropDownField(state = regions(), enabled = false, supportingText = "Select Country first", onSelected = {}) }
        onNodeWithText("Select Country first").assertExists()
        onNodeWithText("Region").performClick()
        onNodeWithText("Arusha").assertDoesNotExist()
    }

    @Test
    fun menuOpenWhenDisabledStaysClosedAfterReenabling() = runComposeUiTest {
        var enabled by mutableStateOf(true)
        setContent { FormInputDropDownField(state = regions(), enabled = enabled, onSelected = {}) }
        onNodeWithText("Region").performClick()
        onNodeWithText("Arusha").assertExists()
        enabled = false
        waitForIdle()
        enabled = true
        waitForIdle()
        onNodeWithText("Arusha").assertDoesNotExist()
    }

    @Test
    fun filledTextFieldShowsItsLabelAndReportsTyping() = runComposeUiTest {
        var state by mutableStateOf(text())
        setContent { FormInputTextField(state = state, style = FormInputFieldStyle.FILLED, onValueChange = { state = it }) }
        onNodeWithText("Field").performTextInput("abc")
        assertEquals("abc", state.value)
    }

    @Test
    fun filledDropDownSelectsOption() = runComposeUiTest {
        var state by mutableStateOf(regions())
        setContent { FormInputDropDownField(state = state, style = FormInputFieldStyle.FILLED, onSelected = { state = it }) }
        onNodeWithText("Region").performClick()
        onNodeWithText("Arusha").performClick()
        assertEquals("2", state.value.id)
    }

    @Test
    fun filledDropDownWithShapeStaysDisabledWhenDisabled() = runComposeUiTest {
        setContent {
            FormInputDropDownField(
                state = regions(), style = FormInputFieldStyle.FILLED, shape = formInputShape(), enabled = false,
                supportingText = "Select Country first", onSelected = {},
            )
        }
        onNodeWithText("Select Country first").assertExists()
        onNodeWithText("Region").performClick()
        onNodeWithText("Arusha").assertDoesNotExist()
    }

    @Test
    fun textFieldValueOverloadFiltersInputAndReportsTheSelection() = runComposeUiTest {
        var value by mutableStateOf(TextFieldValue(""))
        setContent {
            FormInputTextField(state = text(FormInputType.NUMBER), value = value, onValueChange = { value = it })
        }
        onNodeWithText("Field").performTextInput("1a2")
        assertEquals("12", value.text)
        assertEquals(TextRange(2), value.selection)
    }

    @Test
    fun textFieldValueOverloadHonoursACursorSetByTheCaller() = runComposeUiTest {
        var value by mutableStateOf(TextFieldValue("hello", TextRange(5)))
        setContent {
            FormInputTextField(state = text(), value = value, onValueChange = { value = it })
        }
        value = TextFieldValue("hello", TextRange(0))
        waitForIdle()
        onNodeWithText("Field").performTextInput("X")
        assertEquals("Xhello", value.text)
    }

    @Test
    fun textFieldValueOverloadRejectsInputBeyondMaxChar() = runComposeUiTest {
        var value by mutableStateOf(TextFieldValue(""))
        setContent {
            FormInputTextField(state = text(maxChar = 3), value = value, onValueChange = { value = it })
        }
        onNodeWithText("Field").performTextInput("abcdef")
        assertEquals("", value.text)
    }
}
