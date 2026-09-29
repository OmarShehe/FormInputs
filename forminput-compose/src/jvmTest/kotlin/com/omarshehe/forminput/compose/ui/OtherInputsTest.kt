package com.omarshehe.forminput.compose.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.label_uploaded_documents
import com.omarshehe.forminput.compose.resources.select_date
import com.omarshehe.forminput.compose.resources.select_date_time
import com.omarshehe.forminput.compose.resources.select_time
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputImageState
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import com.omarshehe.forminput.compose.ui.utils.formInputShape
import java.awt.image.BufferedImage
import java.io.File
import java.util.Locale
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalTestApi::class)
class OtherInputsTest {
    private fun search(value: String = "") = FormInputTextFieldState(id = "s", type = FormInputType.TEXT, value = value)

    @Test
    fun searchFieldReportsTypingAndClears() = runComposeUiTest {
        var state by mutableStateOf(search())
        setContent { FormInputSearchField(state = state, onValueChange = { state = state.copy(value = it) }) }
        onNodeWithText("Search...").assertExists()
        onNodeWithText("Search...").performTextInput("kivu")
        assertEquals("kivu", state.value)
        onNodeWithContentDescription("Clear search").performClick()
        assertEquals("", state.value)
    }

    @Test
    fun immutableFieldShowsLabelAndText() = runComposeUiTest {
        setContent { FormInputImmutableTextField(label = "Plate", text = "T123 ABC") }
        onNodeWithText("T123 ABC").assertExists()
        onNodeWithText("Plate").assertExists()
    }

    @Test
    fun stepperCountsWithinLimits() = runComposeUiTest {
        var value by mutableStateOf(0)
        setContent { QuantityStepperControl(label = "Seats", value = value, onValueChange = { value = it }, minValue = 0, maxValue = 2) }
        val buttons = onAllNodes(hasClickAction())
        buttons[0].assertIsNotEnabled()
        buttons[1].performClick()
        buttons[1].performClick()
        assertEquals(2, value)
        buttons[1].assertIsNotEnabled()
        buttons[0].assertIsEnabled()
        buttons[0].performClick()
        assertEquals(1, value)
    }

    @Test
    fun buttonClicksAndBlocksWhileLoading() = runComposeUiTest {
        var clicks = 0
        var loading by mutableStateOf(false)
        setContent { FormInputButton(onClick = { clicks++ }, text = "Save", isLoading = loading) }
        onNodeWithText("Save").performClick()
        assertEquals(1, clicks)
        loading = true
        waitForIdle()
        onAllNodes(hasClickAction())[0].assertIsNotEnabled()
    }

    private fun pickerState(type: FormInputType, value: String) = FormInputDateTimePickerState(
        id = "d", labelRes = when (type) {
            FormInputType.DATE_PICKER -> Res.string.select_date
            FormInputType.TIME_PICKER -> Res.string.select_time
            else -> Res.string.select_date_time
        },
        placeholderRes = null, type = type, value = value,
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

    private fun ComposeUiTest.confirmPicker(label: String, steps: List<String>) {
        onNodeWithText(label).performClick()
        steps.forEach { onNodeWithText(it).performClick() }
    }

    @Test
    fun datePickerKeepsTheDateWhenConfirmedUnchanged() = runComposeUiTest {
        var state by mutableStateOf(pickerState(FormInputType.DATE_PICKER, "2026-09-29"))
        setContent { FormInputDatePickerField(state = state, onValueChange = { state = it }) }
        confirmPicker("Select Date", listOf("OK"))
        assertEquals("2026-09-29", state.value)
    }

    @Test
    fun timePickerKeepsTheTimeWhenConfirmedUnchanged() = runComposeUiTest {
        var state by mutableStateOf(pickerState(FormInputType.TIME_PICKER, "14:30"))
        setContent { FormInputTimePickerField(state = state, onValueChange = { state = it }) }
        confirmPicker("Select Time", listOf("OK"))
        assertEquals("14:30", state.value)
    }

    @Test
    fun dateTimePickerShowsDisplayFormatAndKeepsValueWhenConfirmed() = withEnglishLocale {
        runComposeUiTest {
        var state by mutableStateOf(pickerState(FormInputType.DATE_TIME_PICKER, "2026-09-29 14:30"))
        setContent { FormInputDateTimePickerField(state = state, onValueChange = { state = it }) }
        onNodeWithText("29 Sep 2026  14:30").assertExists()
        confirmPicker("Select Date & Time", listOf("Next", "OK"))
        assertEquals("2026-09-29 14:30", state.value)
        }
    }

    @Test
    fun documentFieldShowsPromptThenTheFileAndDeleteClearsIt() = runComposeUiTest {
        var state by mutableStateOf(FormInputFileState(id = "f", labelRes = null, placeholderRes = null))
        setContent { FormInputUploadDocument(state = state, onValueChange = { state = it }) }
        onNodeWithText("Click to upload file").assertExists()
        state = state.copy(value = FormInputFileState.FileUploadValue(filePath = "/x/id.pdf", fileName = "id.pdf", fileSize = "1.5 KB"))
        waitForIdle()
        onNodeWithText("id.pdf").assertExists()
        onNodeWithContentDescription("Delete").performClick()
        assertNull(state.value)
    }

    @Test
    fun multiDocumentListsFiles() = runComposeUiTest {
        val state = FormInputMultiFileState(
            id = "m",
            values = listOf(
                FormInputFileState.FileUploadValue(filePath = "/a.pdf", fileName = "a.pdf"),
                FormInputFileState.FileUploadValue(filePath = "/b.pdf", fileName = "b.pdf"),
            ),
        )
        setContent { FormInputUploadMultiDocument(state = state, onValueChange = {}) }
        onNodeWithText("a.pdf").assertExists()
        onNodeWithText("b.pdf").assertExists()
    }

    @Test
    fun imageFieldShowsPrimaryBadgeAndDeleteForAFilledSlot() = runComposeUiTest {
        val photo = File.createTempFile("photo", ".png").apply {
            deleteOnExit()
            ImageIO.write(BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), "png", this)
        }
        val state = FormInputImageState(
            id = "i", labelRes = Res.string.label_uploaded_documents, placeholderRes = null,
            values = listOf(FormInputImageState.ImageUploadValue(filePath = photo.absolutePath, isPrimary = true), null),
        )
        setContent { FormInputUploadImage(state = state, onValueChange = {}) }
        onNodeWithText("Uploaded Documents").assertExists()
        onNodeWithText("Primary").assertExists()
        onNodeWithContentDescription("Delete").assertExists()
    }

    @Test
    fun colorFieldShowsTheValueAndOpensThePicker() = runComposeUiTest {
        setContent { FormInputColorPickerField(label = Res.string.select_date, value = "#FF0000", onColorSelected = {}) }
        onNodeWithText("#FF0000").assertExists()
        onNodeWithText("Select Date").performClick()
        onNodeWithText("HEX").assertExists()
    }

    @Test
    fun datePickerOpensOnAValueThatArrivesLate() = runComposeUiTest {
        var state by mutableStateOf(pickerState(FormInputType.DATE_PICKER, ""))
        setContent { FormInputDatePickerField(state = state, onValueChange = { state = it }) }
        state = state.copy(value = "2001-03-15")
        waitForIdle()
        confirmPicker("Select Date", listOf("OK"))
        assertEquals("2001-03-15", state.value)
    }

    @Test
    fun timePickerOpensOnAValueThatArrivesLate() = runComposeUiTest {
        var state by mutableStateOf(pickerState(FormInputType.TIME_PICKER, ""))
        setContent { FormInputTimePickerField(state = state, onValueChange = { state = it }) }
        state = state.copy(value = "03:07")
        waitForIdle()
        confirmPicker("Select Time", listOf("OK"))
        assertEquals("03:07", state.value)
    }

    @Test
    fun dateTimePickerOpensOnAValueThatArrivesLate() = withEnglishLocale {
        runComposeUiTest {
            var state by mutableStateOf(pickerState(FormInputType.DATE_TIME_PICKER, ""))
            setContent { FormInputDateTimePickerField(state = state, onValueChange = { state = it }) }
            state = state.copy(value = "2001-03-15 03:07")
            waitForIdle()
            confirmPicker("Select Date & Time", listOf("Next", "OK"))
            assertEquals("2001-03-15 03:07", state.value)
        }
    }

    @Test
    fun filledDatePickerKeepsTheDateWhenConfirmedUnchanged() = runComposeUiTest {
        var state by mutableStateOf(pickerState(FormInputType.DATE_PICKER, "2001-03-15"))
        setContent { FormInputDatePickerField(state = state, style = FormInputFieldStyle.FILLED, onValueChange = { state = it }) }
        confirmPicker("Select Date", listOf("OK"))
        assertEquals("2001-03-15", state.value)
    }

    @Test
    fun filledTimePickerKeepsTheTimeWhenConfirmedUnchanged() = runComposeUiTest {
        var state by mutableStateOf(pickerState(FormInputType.TIME_PICKER, "03:07"))
        setContent { FormInputTimePickerField(state = state, style = FormInputFieldStyle.FILLED, onValueChange = { state = it }) }
        confirmPicker("Select Time", listOf("OK"))
        assertEquals("03:07", state.value)
    }

    @Test
    fun filledDateTimePickerShowsTheDisplayFormatAndKeepsTheValue() = withEnglishLocale {
        runComposeUiTest {
            var state by mutableStateOf(pickerState(FormInputType.DATE_TIME_PICKER, "2001-03-15 03:07"))
            setContent { FormInputDateTimePickerField(state = state, style = FormInputFieldStyle.FILLED, onValueChange = { state = it }) }
            onNodeWithText("15 Mar 2001  03:07").assertExists()
            confirmPicker("Select Date & Time", listOf("Next", "OK"))
            assertEquals("2001-03-15 03:07", state.value)
        }
    }

    @Test
    fun filledColorFieldShowsTheValueAndOpensThePicker() = runComposeUiTest {
        setContent {
            FormInputColorPickerField(
                label = Res.string.select_date, value = "#FF0000", onColorSelected = {},
                style = FormInputFieldStyle.FILLED, shape = formInputShape(),
            )
        }
        onNodeWithText("#FF0000").assertExists()
        onNodeWithText("Select Date").performClick()
        onNodeWithText("HEX").assertExists()
    }

    @Test
    fun searchFieldTextFieldValueOverloadReportsTheSelectionAndClears() = runComposeUiTest {
        var value by mutableStateOf(TextFieldValue(""))
        setContent { FormInputSearchField(state = search(), value = value, onValueChange = { value = it }) }
        onNodeWithText("Search...").performTextInput("kivu")
        assertEquals("kivu", value.text)
        assertEquals(TextRange(4), value.selection)
        onNodeWithContentDescription("Clear search").performClick()
        assertEquals("", value.text)
    }

    @Test
    fun searchFieldTextFieldValueOverloadHonoursACursorSetByTheCaller() = runComposeUiTest {
        var value by mutableStateOf(TextFieldValue("kivu", TextRange(4)))
        setContent { FormInputSearchField(state = search(), value = value, onValueChange = { value = it }) }
        value = TextFieldValue("kivu", TextRange(0))
        waitForIdle()
        onAllNodes(hasSetTextAction())[0].performTextInput("X")
        assertEquals("Xkivu", value.text)
    }

    private fun runStyledSearch(style: FormInputFieldStyle) = runComposeUiTest {
        var state by mutableStateOf(search())
        setContent {
            FormInputSearchField(state = state, style = style, onValueChange = { state = state.copy(value = it) })
        }
        onNodeWithText("Search...").assertExists()
        onNodeWithText("Search...").performTextInput("kivu")
        assertEquals("kivu", state.value)
        onNodeWithContentDescription("Clear search").performClick()
        assertEquals("", state.value)
    }

    @Test
    fun outlinedSearchFieldTypesAndClears() = runStyledSearch(FormInputFieldStyle.OUTLINED)

    @Test
    fun filledSearchFieldTypesAndClears() = runStyledSearch(FormInputFieldStyle.FILLED)

    @Test
    fun styledSearchFieldDropsTheLabelAndKeepsThePlaceholder() = runComposeUiTest {
        setContent {
            FormInputSearchField(
                state = search().copy(label = "Find an address", placeholder = "Type an address"), style = FormInputFieldStyle.FILLED,
                shape = formInputShape(), onValueChange = {},
            )
        }
        onNodeWithText("Type an address").assertExists()
        onNodeWithText("Find an address").assertDoesNotExist()
    }

    private fun runReadOnly(style: FormInputFieldStyle) = runComposeUiTest {
        var clicks = 0
        setContent {
            FormInputImmutableTextField(
                label = "Plate", text = "T123 ABC", style = style, imageVector = Icons.Default.Edit, onIconClick = { clicks++ },
            )
        }
        onNodeWithText("T123 ABC").assertExists()
        onNodeWithText("Plate").assertExists()
        onNodeWithContentDescription("Plate").performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun outlinedReadOnlyFieldShowsItsValueAndHandlesTheIconClick() = runReadOnly(FormInputFieldStyle.OUTLINED)

    @Test
    fun filledReadOnlyFieldShowsItsValueAndHandlesTheIconClick() = runReadOnly(FormInputFieldStyle.FILLED)
}
