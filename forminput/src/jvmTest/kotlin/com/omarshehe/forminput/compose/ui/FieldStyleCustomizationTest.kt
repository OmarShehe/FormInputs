package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omarshehe.forminput.compose.ui.model.DropDownOptionModel
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputDropDownState
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.model.FormInputTextFieldState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import kotlin.test.Test
import kotlin.test.assertTrue

/** Every boxed field takes the same `textStyle` and `contentPadding` as the text field; a larger value makes the field taller. */
@OptIn(ExperimentalTestApi::class)
class FieldStyleCustomizationTest {
    private val bigText = TextStyle(fontSize = 64.sp)
    private val tallPadding = PaddingValues(vertical = 40.dp)

    private fun ComposeUiTest.onNodeWithTagBounds(tag: String): Dp {
        val bounds = onNodeWithTag(tag).getBoundsInRoot()
        return bounds.bottom - bounds.top
    }

    /** Renders [plain] and [changed] one above the other and asserts [changed] is taller. */
    private fun ComposeUiTest.assertTaller(plain: @Composable (Modifier) -> Unit, changed: @Composable (Modifier) -> Unit) {
        setContent {
            Column {
                plain(Modifier.testTag("plain"))
                changed(Modifier.testTag("changed"))
            }
        }
        val before = onNodeWithTagBounds("plain")
        val after = onNodeWithTagBounds("changed")
        assertTrue(after > before, "expected a taller field, got $after against $before")
    }

    private fun picker(type: FormInputType) = FormInputDateTimePickerState(
        id = "d", labelRes = null, placeholderRes = null, type = type, label = "When",
    )

    private val dropDown = FormInputDropDownState(
        id = "r", label = "Region", value = DropDownOptionModel(), options = listOf(DropDownOptionModel("1", "Dodoma")),
    )

    private val text = FormInputTextFieldState(
        id = "s", labelRes = null, placeholderRes = null, type = FormInputType.TEXT, value = "", placeholder = "Find",
    )

    // ── date, time, date-time ─────────────────────────────────────────────────

    @Test
    fun datePickerTakesTextStyleAndContentPadding() {
        runComposeUiTest {
            assertTaller(
                { FormInputDatePickerField(state = picker(FormInputType.DATE_PICKER), onValueChange = {}, modifier = it) },
                { FormInputDatePickerField(state = picker(FormInputType.DATE_PICKER), onValueChange = {}, modifier = it, textStyle = bigText) },
            )
        }
        runComposeUiTest {
            assertTaller(
                { FormInputDatePickerField(state = picker(FormInputType.DATE_PICKER), onValueChange = {}, modifier = it) },
                { FormInputDatePickerField(state = picker(FormInputType.DATE_PICKER), onValueChange = {}, modifier = it, contentPadding = tallPadding) },
            )
        }
    }

    @Test
    fun timePickerTakesTextStyleAndContentPadding() {
        runComposeUiTest {
            assertTaller(
                { FormInputTimePickerField(state = picker(FormInputType.TIME_PICKER), onValueChange = {}, modifier = it) },
                { FormInputTimePickerField(state = picker(FormInputType.TIME_PICKER), onValueChange = {}, modifier = it, textStyle = bigText) },
            )
        }
        runComposeUiTest {
            assertTaller(
                { FormInputTimePickerField(state = picker(FormInputType.TIME_PICKER), onValueChange = {}, modifier = it) },
                { FormInputTimePickerField(state = picker(FormInputType.TIME_PICKER), onValueChange = {}, modifier = it, contentPadding = tallPadding) },
            )
        }
    }

    @Test
    fun dateTimePickerTakesTextStyleAndContentPadding() {
        runComposeUiTest {
            assertTaller(
                { FormInputDateTimePickerField(state = picker(FormInputType.DATE_TIME_PICKER), onValueChange = {}, modifier = it) },
                { FormInputDateTimePickerField(state = picker(FormInputType.DATE_TIME_PICKER), onValueChange = {}, modifier = it, textStyle = bigText) },
            )
        }
        runComposeUiTest {
            assertTaller(
                { FormInputDateTimePickerField(state = picker(FormInputType.DATE_TIME_PICKER), onValueChange = {}, modifier = it) },
                { FormInputDateTimePickerField(state = picker(FormInputType.DATE_TIME_PICKER), onValueChange = {}, modifier = it, contentPadding = tallPadding) },
            )
        }
    }

    @Test
    fun dateTimePickerShowsItsSupportingTextAndHonoursEnabled() = runComposeUiTest {
        setContent {
            Column {
                FormInputDateTimePickerField(
                    state = picker(FormInputType.DATE_TIME_PICKER), onValueChange = {}, supportingText = "Pick a moment",
                )
                FormInputDateTimePickerField(
                    state = picker(FormInputType.DATE_TIME_PICKER).copy(label = "Closed"), onValueChange = {}, enabled = false,
                )
            }
        }
        onNodeWithText("Pick a moment").assertExists()
        onNodeWithText("Closed").performClick()
        onNodeWithText("OK").assertDoesNotExist()
    }

    // ── dropdown, colour picker ───────────────────────────────────────────────

    @Test
    fun dropDownTakesContentPadding() = runComposeUiTest {
        assertTaller(
            { FormInputDropDownField(modifier = it, state = dropDown, onSelected = {}) },
            { FormInputDropDownField(modifier = it, state = dropDown, onSelected = {}, contentPadding = tallPadding) },
        )
    }

    @Test
    fun colorPickerTakesTextStyleAndContentPadding() {
        runComposeUiTest {
            assertTaller(
                { FormInputColorPickerField(label = "C", value = "#FF0000", onColorSelected = {}, modifier = it) },
                { FormInputColorPickerField(label = "C", value = "#FF0000", onColorSelected = {}, modifier = it, textStyle = bigText) },
            )
        }
        runComposeUiTest {
            assertTaller(
                { FormInputColorPickerField(label = "C", value = "#FF0000", onColorSelected = {}, modifier = it) },
                { FormInputColorPickerField(label = "C", value = "#FF0000", onColorSelected = {}, modifier = it, contentPadding = tallPadding) },
            )
        }
    }

    // ── search ────────────────────────────────────────────────────────────────

    @Test
    fun styledSearchFieldTakesTextStyleAndContentPadding() {
        runComposeUiTest {
            assertTaller(
                { FormInputSearchField(state = text, onValueChange = {}, modifier = it, style = FormInputFieldStyle.OUTLINED) },
                { FormInputSearchField(state = text, onValueChange = {}, modifier = it, style = FormInputFieldStyle.OUTLINED, textStyle = bigText) },
            )
        }
        runComposeUiTest {
            assertTaller(
                { FormInputSearchField(state = text, onValueChange = {}, modifier = it, style = FormInputFieldStyle.OUTLINED) },
                { FormInputSearchField(state = text, onValueChange = {}, modifier = it, style = FormInputFieldStyle.OUTLINED, contentPadding = tallPadding) },
            )
        }
    }

    @Test
    fun pillSearchFieldTakesTextStyleAndContentPadding() {
        runComposeUiTest {
            assertTaller(
                { FormInputSearchField(state = text, onValueChange = {}, modifier = it) },
                { FormInputSearchField(state = text, onValueChange = {}, modifier = it, textStyle = bigText) },
            )
        }
        runComposeUiTest {
            assertTaller(
                { FormInputSearchField(state = text, onValueChange = {}, modifier = it) },
                { FormInputSearchField(state = text, onValueChange = {}, modifier = it, contentPadding = tallPadding) },
            )
        }
    }
}
