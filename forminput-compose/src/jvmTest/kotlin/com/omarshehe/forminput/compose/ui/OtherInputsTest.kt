package com.omarshehe.forminput.compose.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.label_uploaded_documents
import com.omarshehe.forminput.compose.resources.select_date
import com.omarshehe.forminput.compose.resources.select_date_time
import com.omarshehe.forminput.compose.resources.select_time
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputImageState
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
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
    fun dateTimePickerShowsDisplayFormatAndKeepsValueWhenConfirmed() = runComposeUiTest {
        Locale.setDefault(Locale.ENGLISH)
        var state by mutableStateOf(pickerState(FormInputType.DATE_TIME_PICKER, "2026-09-29 14:30"))
        setContent { FormInputDateTimePickerField(state = state, onValueChange = { state = it }) }
        onNodeWithText("29 Sep 2026  14:30").assertExists()
        confirmPicker("Select Date & Time", listOf("Next", "OK"))
        assertEquals("2026-09-29 14:30", state.value)
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
}
