package com.omarshehe.forminput.compose.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.height
import com.omarshehe.forminput.compose.ui.model.FormInputDateTimePickerState
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
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

    private fun assertTapLayerMatchesTheField(scale: Float) = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = scale)) {
                FormInputDatePickerField(state = date(), onValueChange = {}, supportingText = "hint")
            }
        }
        val field = onNodeWithText("When").fetchSemanticsNode().boundsInRoot
        val layers = onAllNodes(hasClickAction()).fetchSemanticsNodes().map { it.boundsInRoot }
        // The field's own node and the tap layer over it are the full-width clickable nodes; both must span exactly the drawn box.
        val fullWidth = layers.filter { kotlin.math.abs(it.width - field.width) < 1f }
        kotlin.test.assertTrue(fullWidth.size >= 2, "expected the field and a tap layer, found $fullWidth")
        fullWidth.forEach {
            kotlin.test.assertTrue(
                kotlin.math.abs(it.top - field.top) < 1f && kotlin.math.abs(it.bottom - field.bottom) < 1f,
                "a tap layer $it does not match the field $field",
            )
        }
    }

    @Test
    fun theTapLayerMatchesTheDrawnBoxAtNormalSize() = assertTapLayerMatchesTheField(1f)

    @Test
    fun theTapLayerMatchesTheDrawnBoxAtALargeFontSize() = assertTapLayerMatchesTheField(1.8f)

    @Test
    fun theTapLayerMatchesTheDrawnBoxAtTheLargestFontSize() = assertTapLayerMatchesTheField(2.5f)

    @Test
    fun theButtonGrowsWithLargeText() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2.5f)) {
                FormInputButton(onClick = {}, text = "Save")
            }
        }
        val node = onNodeWithText("Save", useUnmergedTree = true).fetchSemanticsNode()
        val layouts = mutableListOf<TextLayoutResult>()
        node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
        val natural = layouts.first().multiParagraph.height
        val shown = node.boundsInRoot.height
        // A fixed-height button squeezes the text into what is left after padding, so it shows less than its natural height.
        kotlin.test.assertTrue(shown >= natural - 1f, "the text is clipped: shown $shown of $natural")
    }

    @Test
    fun theUploadKeepsItsContentBelowTheLabelAtALargeFontSize() = runComposeUiTest {
        val state = FormInputFileState(id = "f", labelRes = null, placeholderRes = null, label = "Passport", placeholder = "Choose a file")
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 1.8f)) {
                FormInputUploadDocument(state = state, onValueChange = {})
            }
        }
        val label = onNodeWithText("Passport", useUnmergedTree = true).getBoundsInRoot().bottom.value
        val content = onNodeWithText("Choose a file", useUnmergedTree = true).getBoundsInRoot().top.value
        kotlin.test.assertTrue(content >= label, "the content starts at $content, above the label's bottom at $label")
    }
}
