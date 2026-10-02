package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.v2.runComposeUiTest
import com.omarshehe.forminput.compose.ui.model.FormInputFileState
import com.omarshehe.forminput.compose.ui.model.FormInputImageState
import com.omarshehe.forminput.compose.ui.model.FormInputMultiFileState
import kotlin.test.Test

/** A control that is only an icon has to say what it does to a screen reader; the texts are overridable like every other. */
@OptIn(ExperimentalTestApi::class)
class AccessibilityTest {
    @Test
    fun stepperButtonsAreNamed() = runComposeUiTest {
        setContent { QuantityStepperControl(label = "Guests", value = 2, onValueChange = {}) }
        onNodeWithContentDescription("Increase").assertExists()
        onNodeWithContentDescription("Decrease").assertExists()
    }

    @Test
    fun stepperButtonNamesCanBeReplaced() = runComposeUiTest {
        setContent {
            FormInputTheme(FormInputDefaults(strings = FormInputStrings(quantityIncrease = "Plus", quantityDecrease = "Minus"))) {
                QuantityStepperControl(label = "Guests", value = 2, onValueChange = {})
            }
        }
        onNodeWithContentDescription("Plus").assertExists()
        onNodeWithContentDescription("Minus").assertExists()
    }

    @Test
    fun imageViewerButtonsAreNamed() = runComposeUiTest {
        setContent { ZoomableImageViewer(imageUrl = "", onDismiss = {}) }
        onNodeWithContentDescription("Close").assertExists()
        onNodeWithContentDescription("Zoom in").assertExists()
        onNodeWithContentDescription("Zoom out").assertExists()
    }

    @Test
    fun emptyImageSlotSaysItAddsAPhoto() = runComposeUiTest {
        setContent {
            FormInputUploadImage(
                state = FormInputImageState(id = "i", labelRes = null, placeholderRes = null, values = listOf(null, null)),
                onValueChange = {},
            )
        }
        onAllNodesWithContentDescription("Add photo")[0].assertExists()
    }

    @Test
    fun uploadedDocumentDeleteButtonIsNamed() = runComposeUiTest {
        val file = FormInputFileState.FileUploadValue(filePath = "/tmp/a.pdf", fileName = "a.pdf")
        setContent { FormInputUploadMultiDocument(state = FormInputMultiFileState(id = "m", values = listOf(file)), onValueChange = {}) }
        onNodeWithContentDescription("Delete").assertExists()
    }

    @Test
    fun newDescriptionsCanBeReplacedForTheViewerAndTheSlot() = runComposeUiTest {
        setContent {
            FormInputTheme(FormInputDefaults(strings = FormInputStrings(close = "Shut", zoomIn = "Bigger", zoomOut = "Smaller", addPhoto = "Take"))) {
                Column {
                    ZoomableImageViewer(imageUrl = "", onDismiss = {})
                    FormInputUploadImage(
                        state = FormInputImageState(id = "i", labelRes = null, placeholderRes = null, values = listOf(null)),
                        onValueChange = {},
                    )
                }
            }
        }
        onNodeWithContentDescription("Shut").assertExists()
        onNodeWithContentDescription("Bigger").assertExists()
        onNodeWithContentDescription("Smaller").assertExists()
        onAllNodesWithContentDescription("Take")[0].assertExists()
    }
}
