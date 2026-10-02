package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.ui.model.FormInputImageState
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import com.omarshehe.forminput.compose.ui.utils.FormInputTestTags
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Sizes the library draws by default can be changed by passing a plain [Modifier]; the caller's modifier always wins. */
@OptIn(ExperimentalTestApi::class)
class ModifierCustomizationTest {
    private fun noSlots() = FormInputImageState(id = "i", labelRes = null, placeholderRes = null, values = emptyList())

    private fun twoSlots() = FormInputImageState(id = "i", labelRes = null, placeholderRes = null, values = listOf(null, null))

    // ── image slots ───────────────────────────────────────────────────────────

    @Test
    fun unboundedSlotIsTodaysSizeByDefault() = runComposeUiTest {
        setContent { FormInputUploadImage(state = noSlots(), onValueChange = {}, unboundedAdd = true) }
        onAllNodesWithTag(FormInputTestTags.ImageSlot)[0].assertWidthIsEqualTo(140.dp)
        onAllNodesWithTag(FormInputTestTags.ImageSlot)[0].assertHeightIsEqualTo(140.dp)
    }

    @Test
    fun unboundedSlotTakesTheCallersSize() = runComposeUiTest {
        setContent { FormInputUploadImage(state = noSlots(), onValueChange = {}, unboundedAdd = true, slotModifier = Modifier.size(96.dp)) }
        onAllNodesWithTag(FormInputTestTags.ImageSlot)[0].assertWidthIsEqualTo(96.dp)
        onAllNodesWithTag(FormInputTestTags.ImageSlot)[0].assertHeightIsEqualTo(96.dp)
    }

    @Test
    fun fixedCountSlotsFillTheRowByDefault() = runComposeUiTest {
        setContent { FormInputUploadImage(state = twoSlots(), onValueChange = {}) }
        onAllNodesWithTag(FormInputTestTags.ImageSlot)[0].assertWidthIsAtLeast(300.dp)
    }

    @Test
    fun fixedCountSlotsCanBeCappedWithAModifier() = runComposeUiTest {
        setContent { FormInputUploadImage(state = twoSlots(), onValueChange = {}, slotModifier = Modifier.widthIn(max = 96.dp)) }
        onAllNodesWithTag(FormInputTestTags.ImageSlot)[0].assertWidthIsEqualTo(96.dp)
        onAllNodesWithTag(FormInputTestTags.ImageSlot)[0].assertHeightIsEqualTo(96.dp)
    }

    @Test
    fun cappedSlotsNeverExceedTheirShareOfANarrowRow() = runComposeUiTest {
        setContent {
            Box(Modifier.width(100.dp)) {
                FormInputUploadImage(state = twoSlots(), onValueChange = {}, slotModifier = Modifier.widthIn(max = 96.dp))
            }
        }
        // 100dp row, two slots, 16dp gap: each slot gets 42dp, below the 96dp cap.
        onAllNodesWithTag(FormInputTestTags.ImageSlot)[0].assertWidthIsEqualTo(42.dp)
    }

    @Test
    fun slotSpacingDefaultsToSixteenAndCanBeChanged() = runComposeUiTest {
        setContent {
            Column {
                FormInputUploadImage(state = twoSlots(), onValueChange = {}, slotModifier = Modifier.widthIn(max = 96.dp))
                FormInputUploadImage(
                    state = twoSlots(), onValueChange = {}, slotModifier = Modifier.widthIn(max = 96.dp), slotSpacing = 40.dp,
                )
            }
        }
        fun gap(first: Int): Dp {
            val left = onAllNodesWithTag(FormInputTestTags.ImageSlot)[first].getBoundsInRoot()
            val right = onAllNodesWithTag(FormInputTestTags.ImageSlot)[first + 1].getBoundsInRoot()
            return right.left - left.right
        }
        assertEquals(16.dp, gap(0))
        assertEquals(40.dp, gap(2))
    }

    // ── multi-document upload area ────────────────────────────────────────────

    @Test
    fun uploadAreaIsTodaysHeightByDefault() = runComposeUiTest {
        setContent { FormInputUploadMultiDocument(state = FormInputMultiFileState(id = "m"), onValueChange = {}) }
        onNodeWithTag(FormInputTestTags.UploadArea).assertHeightIsEqualTo(120.dp)
    }

    @Test
    fun uploadAreaTakesTheCallersHeight() = runComposeUiTest {
        setContent {
            FormInputUploadMultiDocument(state = FormInputMultiFileState(id = "m"), onValueChange = {}, areaModifier = Modifier.height(80.dp))
        }
        onNodeWithTag(FormInputTestTags.UploadArea).assertHeightIsEqualTo(80.dp)
    }

    // ── button ────────────────────────────────────────────────────────────────

    @Test
    fun buttonMinimumWidthIsTodaysByDefault() = runComposeUiTest {
        setContent { FormInputButton(onClick = {}, text = "Go") }
        onNode(hasClickAction()).assertWidthIsEqualTo(130.dp)
    }

    @Test
    fun buttonTakesTheCallersWidthWiderOrNarrower() = runComposeUiTest {
        setContent {
            Column {
                FormInputButton(onClick = {}, text = "A", modifier = Modifier.width(200.dp))
                FormInputButton(onClick = {}, text = "B", modifier = Modifier.width(100.dp))
            }
        }
        onNode(hasClickAction() and hasText("A")).assertWidthIsEqualTo(200.dp)
        onNode(hasClickAction() and hasText("B")).assertWidthIsEqualTo(100.dp)
    }

    // ── colour picker popup ───────────────────────────────────────────────────

    @Test
    fun colorPickerPopupIsTodaysSizeByDefault() = runComposeUiTest {
        setContent { FormInputColorPickerField(label = "A", value = "#FF0000", onColorSelected = {}) }
        onNodeWithText("A").performClick()
        onNodeWithTag(FormInputTestTags.ColorPickerPopup).assertWidthIsEqualTo(300.dp)
        onNodeWithTag(FormInputTestTags.ColorSpectrum).assertHeightIsEqualTo(180.dp)
    }

    @Test
    fun colorPickerPopupTakesTheCallersModifiers() = runComposeUiTest {
        setContent {
            FormInputColorPickerField(
                label = "B", value = "#FF0000", onColorSelected = {},
                popupModifier = Modifier.width(260.dp), spectrumModifier = Modifier.height(120.dp),
            )
        }
        onNodeWithText("B").performClick()
        onNodeWithTag(FormInputTestTags.ColorPickerPopup).assertWidthIsEqualTo(260.dp)
        onNodeWithTag(FormInputTestTags.ColorSpectrum).assertHeightIsEqualTo(120.dp)
    }

    @Test
    fun multiDocumentAddsNoSpaceBelowItsAreaByDefault() = runComposeUiTest {
        setContent {
            FormInputUploadMultiDocument(state = FormInputMultiFileState(id = "m"), onValueChange = {}, modifier = Modifier.testTag("whole"))
        }
        onNodeWithTag("whole").assertHeightIsEqualTo(120.dp)
    }

    @Test
    fun slotSpacingIsExactToWithinHalfADp() = runComposeUiTest {
        setContent { FormInputUploadImage(state = twoSlots(), onValueChange = {}, slotModifier = Modifier.widthIn(max = 96.dp)) }
        val left = onAllNodesWithTag(FormInputTestTags.ImageSlot)[0].getBoundsInRoot()
        val right = onAllNodesWithTag(FormInputTestTags.ImageSlot)[1].getBoundsInRoot()
        assertTrue(kotlin.math.abs((right.left - left.right).value - 16f) < 0.5f)
    }
}
