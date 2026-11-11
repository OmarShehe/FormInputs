package com.omarshehe.forminput.compose.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.clear_search
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import kotlin.test.Test
import kotlin.test.assertEquals

/** The header action row (a small text button above the field) that the text field has always had is available on the date, date-time and time pickers too. */
@OptIn(ExperimentalTestApi::class)
class PickerHeaderActionTest {
    private val actionLabel = "Clear search"

    private fun state(type: FormInputType, onClick: (() -> Unit)?) = FormInputDateTimePickerState(
        id = "d",
        labelRes = null,
        placeholderRes = null,
        type = type,
        label = "Expiry",
        onHeaderActionClick = onClick,
        headerActionLabelRes = Res.string.clear_search.takeIf { onClick != null },
        headerActionIcon = Icons.Default.Clear.takeIf { onClick != null },
    )

    @Test
    fun datePickerShowsTheActionAndRunsItOnClick() = runComposeUiTest {
        var clicks = 0
        setContent { FormInputDatePickerField(state(FormInputType.DATE_PICKER) { clicks++ }, onValueChange = {}) }
        onNodeWithText(actionLabel).assertIsDisplayed()
        onNodeWithText(actionLabel).performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun dateTimePickerShowsTheActionAndRunsItOnClick() = runComposeUiTest {
        var clicks = 0
        setContent { FormInputDateTimePickerField(state(FormInputType.DATE_TIME_PICKER) { clicks++ }, onValueChange = {}) }
        onNodeWithText(actionLabel).assertIsDisplayed()
        onNodeWithText(actionLabel).performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun timePickerShowsTheActionAndRunsItOnClick() = runComposeUiTest {
        var clicks = 0
        setContent { FormInputTimePickerField(state(FormInputType.TIME_PICKER) { clicks++ }, onValueChange = {}) }
        onNodeWithText(actionLabel).assertIsDisplayed()
        onNodeWithText(actionLabel).performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun noActionIsDrawnUnlessOneIsSet() = runComposeUiTest {
        setContent {
            FormInputDatePickerField(state(FormInputType.DATE_PICKER, null), onValueChange = {})
            FormInputDateTimePickerField(state(FormInputType.DATE_TIME_PICKER, null), onValueChange = {})
            FormInputTimePickerField(state(FormInputType.TIME_PICKER, null), onValueChange = {})
        }
        onNodeWithText(actionLabel).assertDoesNotExist()
    }

    @Test
    fun theActionDoesNotOpenThePickerOrChangeTheValue() = runComposeUiTest {
        var changed = 0
        setContent { FormInputDatePickerField(state(FormInputType.DATE_PICKER) {}, onValueChange = { changed++ }) }
        onNodeWithText(actionLabel).performClick()
        onNodeWithText("Cancel").assertDoesNotExist()
        assertEquals(0, changed)
    }

    @Test
    fun theTextFieldKeepsItsHeaderActionAfterTheRowWasShared() = runComposeUiTest {
        var clicks = 0
        setContent {
            FormInputTextField(
                state = FormInputTextFieldState(
                    id = "t",
                    labelRes = null,
                    type = FormInputType.TEXT,
                    label = "Name",
                    value = "",
                    onHeaderActionClick = { clicks++ },
                    headerActionLabelRes = Res.string.clear_search,
                ),
                onValueChange = {},
            )
        }
        onNodeWithText(actionLabel).performClick()
        assertEquals(1, clicks)
    }
}
