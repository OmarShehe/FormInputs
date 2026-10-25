package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import kotlin.test.Test

/** An empty end-date picker opens on its minimum date's month, even when the minimum arrives after first composition. */
@OptIn(ExperimentalTestApi::class)
class PickerOpensOnMinDateTest {
    // 2030-03-10 UTC midnight: far enough ahead that "today" can never be the same month.
    private val march2030 = 1_899_158_400_000L

    private fun state(type: FormInputType, minDateMillis: Long?) = FormInputDateTimePickerState(
        id = "end", labelRes = null, placeholderRes = null, type = type, label = "End", minDateMillis = minDateMillis,
    )

    @Test
    fun datePickerOpensOnTheMonthOfAMinimumSetAfterFirstComposition() = runComposeUiTest {
        var min by mutableStateOf<Long?>(null)
        setContent {
            FormInputDatePickerField(state = state(FormInputType.DATE_PICKER, min), onValueChange = {})
        }
        min = march2030
        waitForIdle()
        onNodeWithText("End").performClick()
        onNodeWithText("March 2030", substring = true).assertExists()
    }

    @Test
    fun dateTimePickerOpensOnTheMonthOfAMinimumSetAfterFirstComposition() = runComposeUiTest {
        var min by mutableStateOf<Long?>(null)
        setContent {
            FormInputDateTimePickerField(state = state(FormInputType.DATE_TIME_PICKER, min), onValueChange = {})
        }
        min = march2030
        waitForIdle()
        onNodeWithText("End").performClick()
        onNodeWithText("March 2030", substring = true).assertExists()
    }
}
