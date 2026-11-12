package com.omarshehe.forminput.compose.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** The viewer's own close button can be left out when the window around it already has one. */
@OptIn(ExperimentalTestApi::class)
class ZoomableImageViewerCloseTest {
    private val closeLabel = "Close"

    @Test
    fun closeButtonIsShownByDefaultAndDismisses() = runComposeUiTest {
        var dismissed = 0
        setContent { ZoomableImageViewer(imageUrl = "https://example.invalid/a.png", onDismiss = { dismissed++ }) }
        onNodeWithContentDescription(closeLabel).assertIsDisplayed()
        onNodeWithContentDescription(closeLabel).performClick()
        assertEquals(1, dismissed)
    }

    @Test
    fun closeButtonIsLeftOutWhenAskedTo() = runComposeUiTest {
        setContent { ZoomableImageViewer(imageUrl = "https://example.invalid/a.png", onDismiss = {}, showCloseButton = false) }
        onNodeWithContentDescription(closeLabel).assertDoesNotExist()
    }
}
