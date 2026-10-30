package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

/** The date fields show the stored value in the caller's display pattern, while the stored value keeps its fixed format. */
@OptIn(ExperimentalTestApi::class)
class DatePickerDisplayFormatTest {
    private val numeric = "dd/MM/yyyy"

    private fun state(
        type: FormInputType,
        value: String = "",
        manual: Boolean = false,
        minDateMillis: Long? = null,
    ) = FormInputDateTimePickerState(
        id = "d", labelRes = null, placeholderRes = null, type = type, label = "When", value = value,
        isManualEditable = manual, minDateMillis = minDateMillis,
    )

    private fun withEnglishLocale(block: () -> Unit) {
        val previous = Locale.getDefault()
        Locale.setDefault(Locale.ENGLISH)
        try {
            block()
        } finally {
            Locale.setDefault(previous)
        }
    }

    private fun ComposeUiTest.confirm(steps: List<String>) {
        onNodeWithText("When").performClick()
        steps.forEach { onNodeWithText(it).performClick() }
    }

    // ---- date-only field

    @Test
    fun datePickerShowsTheValueInTheDisplayFormat() = runComposeUiTest {
        setContent {
            FormInputDatePickerField(state = state(FormInputType.DATE_PICKER, "2026-12-01"), displayFormat = numeric, onValueChange = {})
        }
        onNodeWithText("01/12/2026").assertExists()
    }

    @Test
    fun datePickerWithNoFormatShowsIso() = runComposeUiTest {
        setContent { FormInputDatePickerField(state = state(FormInputType.DATE_PICKER, "2026-12-01"), onValueChange = {}) }
        onNodeWithText("2026-12-01").assertExists()
    }

    @Test
    fun pickingADateKeepsIsoInTheValue() = runComposeUiTest {
        var current by mutableStateOf(state(FormInputType.DATE_PICKER, "2026-12-01"))
        setContent { FormInputDatePickerField(state = current, displayFormat = numeric, onValueChange = { current = it }) }
        confirm(listOf("OK"))
        assertEquals("2026-12-01", current.value)
        onNodeWithText("01/12/2026").assertExists()
    }

    @Test
    fun changingTheFormatRedrawsTheDateWithoutChangingTheValue() = withEnglishLocale {
        runComposeUiTest {
            var format by mutableStateOf(numeric)
            var calls = 0
            setContent {
                FormInputDatePickerField(
                    state = state(FormInputType.DATE_PICKER, "2026-12-01"), displayFormat = format, onValueChange = { calls++ },
                )
            }
            onNodeWithText("01/12/2026").assertExists()
            format = "dd MMM yyyy"
            waitForIdle()
            onNodeWithText("01 Dec 2026").assertExists()
            onNodeWithText("01/12/2026").assertDoesNotExist()
            assertEquals(0, calls)
        }
    }

    @Test
    fun anEmptyDateShowsNothingInEveryFormat() = runComposeUiTest {
        var format by mutableStateOf(numeric)
        setContent { FormInputDatePickerField(state = state(FormInputType.DATE_PICKER), displayFormat = format, onValueChange = {}) }
        onNodeWithText("null", substring = true).assertDoesNotExist()
        format = "dd MMM yyyy"
        waitForIdle()
        onNodeWithText("null", substring = true).assertDoesNotExist()
        onNodeWithText("Jan", substring = true).assertDoesNotExist()
    }

    @Test
    fun anInvalidDateValueIsShownAsItIs() = runComposeUiTest {
        setContent {
            FormInputDatePickerField(state = state(FormInputType.DATE_PICKER, "garbage"), displayFormat = numeric, onValueChange = {})
        }
        onNodeWithText("garbage").assertExists()
    }

    @Test
    fun typingADateInTheFormatStoresIso() = runComposeUiTest {
        var current by mutableStateOf(state(FormInputType.DATE_PICKER, manual = true))
        setContent { FormInputDatePickerField(state = current, displayFormat = numeric, onValueChange = { current = it }) }
        onNodeWithText("When").performTextInput("01/12/2026")
        assertEquals("2026-12-01", current.value)
        onNodeWithText("01/12/2026").assertExists()
    }

    @Test
    fun typingAnInvalidDateLeavesTheStoredValue() = runComposeUiTest {
        var current by mutableStateOf(state(FormInputType.DATE_PICKER, manual = true))
        setContent { FormInputDatePickerField(state = current, displayFormat = numeric, onValueChange = { current = it }) }
        onNodeWithText("When").performTextInput("01/13/2026")
        assertEquals("", current.value)
        onNodeWithText("01/13/2026").assertExists()
    }

    @Test
    fun typingOverAStoredDateWithABadOneKeepsTheOldValue() = runComposeUiTest {
        var current by mutableStateOf(state(FormInputType.DATE_PICKER, "2026-12-01", manual = true))
        setContent { FormInputDatePickerField(state = current, displayFormat = numeric, onValueChange = { current = it }) }
        onNodeWithText("01/12/2026").performTextInput("x")
        assertEquals("2026-12-01", current.value)
    }

    @Test
    fun clearingTheTextClearsTheValue() = runComposeUiTest {
        var current by mutableStateOf(state(FormInputType.DATE_PICKER, "2026-12-01", manual = true))
        setContent { FormInputDatePickerField(state = current, displayFormat = numeric, onValueChange = { current = it }) }
        onNodeWithText("01/12/2026").performTextClearance()
        assertEquals("", current.value)
    }

    @Test
    fun typingTheSameDateDoesNotCallBack() = runComposeUiTest {
        var calls = 0
        setContent {
            FormInputDatePickerField(
                state = state(FormInputType.DATE_PICKER, "2026-12-01", manual = true), displayFormat = numeric, onValueChange = { calls++ },
            )
        }
        onNodeWithText("01/12/2026").performTextInput("")
        assertEquals(0, calls)
    }

    @Test
    fun minimumDateStillAppliesWithADisplayFormat() = runComposeUiTest {
        val farFutureMarch = LocalDate(2090, 3, 10).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        setContent {
            FormInputDatePickerField(
                state = state(FormInputType.DATE_PICKER, minDateMillis = farFutureMarch), displayFormat = numeric, onValueChange = {},
            )
        }
        confirm(emptyList())
        onNodeWithText("March 2090", substring = true).assertExists()
    }

    // ---- date-time field

    @Test
    fun dateTimePickerRedrawsWhenTheFormatChanges() = withEnglishLocale {
        runComposeUiTest {
            var format by mutableStateOf("dd/MM/yyyy, HH:mm")
            var calls = 0
            setContent {
                FormInputDateTimePickerField(
                    state = state(FormInputType.DATE_TIME_PICKER, "2026-12-01 14:30"), displayFormat = format, onValueChange = { calls++ },
                )
            }
            onNodeWithText("01/12/2026, 14:30").assertExists()
            format = "dd MMM yyyy  HH:mm"
            waitForIdle()
            onNodeWithText("01 Dec 2026  14:30").assertExists()
            assertEquals(0, calls)
        }
    }

    // ---- FormInputField dispatcher

    @Test
    fun dispatcherPassesTheDateFormatToDatePickers() = runComposeUiTest {
        setContent {
            FormInputField(
                state = state(FormInputType.DATE_PICKER, "2026-12-01"), onValueChange = {}, dateDisplayFormat = numeric,
            )
        }
        onNodeWithText("01/12/2026").assertExists()
    }

    @Test
    fun dispatcherPassesTheDateTimeFormatToDateTimePickers() = runComposeUiTest {
        setContent {
            FormInputField(
                state = state(FormInputType.DATE_TIME_PICKER, "2026-12-01 14:30"), onValueChange = {},
                dateTimeDisplayFormat = "dd/MM/yyyy, HH:mm",
            )
        }
        onNodeWithText("01/12/2026, 14:30").assertExists()
    }

    @Test
    fun dispatcherWithNoFormatsKeepsTheDefaults() = withEnglishLocale {
        runComposeUiTest {
            setContent {
                FormInputField(state = state(FormInputType.DATE_PICKER, "2026-12-01"), onValueChange = {})
                FormInputField(state = state(FormInputType.DATE_TIME_PICKER, "2026-12-01 14:30"), onValueChange = {})
            }
            onNodeWithText("2026-12-01").assertExists()
            onNodeWithText("01 Dec 2026  14:30").assertExists()
        }
    }

    @Test
    fun eachDispatcherFormatOnlyReachesItsOwnPicker() = withEnglishLocale {
        runComposeUiTest {
            setContent {
                FormInputField(
                    state = state(FormInputType.DATE_PICKER, "2026-12-01"), onValueChange = {}, dateTimeDisplayFormat = "dd/MM/yyyy, HH:mm",
                )
                FormInputField(
                    state = state(FormInputType.DATE_TIME_PICKER, "2026-12-01 14:30"), onValueChange = {}, dateDisplayFormat = numeric,
                )
            }
            onNodeWithText("2026-12-01").assertExists()
            onNodeWithText("01 Dec 2026  14:30").assertExists()
        }
    }
}
