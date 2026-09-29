package com.omarshehe.forminput.compose.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputPriceState
import com.omarshehe.forminput.compose.ui.model.FormInputType
import kotlin.test.Test

/** Regression tests for the problems found in the FIC-001 code review. */
@OptIn(ExperimentalTestApi::class)
class ReviewFixesTest {
    private fun date() = FormInputDateTimePickerState(
        id = "d", labelRes = null, placeholderRes = null, type = FormInputType.DATE_PICKER, label = "When",
    )

    private val largeText = Density(density = 1f, fontScale = 2.5f)

    @Test
    fun aTypographyWithoutALineHeightDoesNotCrashAnOutlinedField() = runComposeUiTest {
        setContent {
            MaterialTheme(typography = Typography(bodySmall = TextStyle())) {
                FormInputDatePickerField(state = date(), onValueChange = {})
            }
        }
        onNodeWithText("When").assertExists()
    }

    @Test
    fun anyPartOfATallPickerOpensItAtALargeFontSize() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides largeText) {
                FormInputDatePickerField(state = date(), onValueChange = {}, supportingText = "hint")
            }
        }
        onRoot().performTouchInput { click(Offset(width / 2f, height * 0.55f)) }
        onNodeWithText("OK").assertExists()
    }

    @Test
    fun theSupportingTextBelowAPickerDoesNotOpenIt() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides largeText) {
                FormInputDatePickerField(state = date(), onValueChange = {}, supportingText = "hint")
            }
        }
        onNodeWithText("hint").performTouchInput { click() }
        onNodeWithText("OK").assertDoesNotExist()
    }

    @Test
    fun aTallColourPickerOpensFromItsLowerHalf() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides largeText) {
                FormInputColorPickerField(label = "Paint", value = "#FF0000", onColorSelected = {}, supportingText = "hint")
            }
        }
        onRoot().performTouchInput { click(Offset(width / 2f, height * 0.55f)) }
        onNodeWithText("HEX").assertExists()
    }

    @Test
    fun theCurrencySelectorIsAnnouncedAsADropdown() = runComposeUiTest {
        val state = FormInputPriceState(id = "p", currency = "TSH", currencies = listOf("TSH", "USD"), label = "Price")
        setContent { FormInputPriceField(state = state, onValueChange = {}) }
        onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList)).assertExists()
    }
}
