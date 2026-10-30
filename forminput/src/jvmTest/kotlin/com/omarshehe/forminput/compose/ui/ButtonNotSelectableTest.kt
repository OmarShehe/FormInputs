package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.asAwtTransferable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import java.awt.datatransfer.DataFlavor
import kotlin.test.Test
import kotlin.test.assertEquals

/** A drag-selection across a button copies the text around it but not the button's label. */
@OptIn(ExperimentalTestApi::class, ExperimentalComposeUiApi::class)
class ButtonNotSelectableTest {
    private val isMac = System.getProperty("os.name").orEmpty().startsWith("Mac", ignoreCase = true)

    /** Records what is copied, so the test needs no system clipboard (there is none on a display-less CI runner). */
    private class RecordingClipboard : Clipboard {
        var copied = ""

        override suspend fun getClipEntry(): ClipEntry? = null

        override suspend fun setClipEntry(clipEntry: ClipEntry?) {
            copied = clipEntry?.asAwtTransferable?.getTransferData(DataFlavor.stringFlavor) as? String ?: ""
        }
    }

    private fun dragSelectAndCopy(style: FormInputButtonStyle): String {
        val clipboard = RecordingClipboard()
        runComposeUiTest {
            setContent {
                CompositionLocalProvider(LocalClipboard provides clipboard) {
                    SelectionContainer {
                        Column {
                            Text("TopText")
                            Spacer(Modifier.height(8.dp))
                            FormInputButton(text = "BtnLabel", onClick = {}, style = style)
                            Spacer(Modifier.height(8.dp))
                            Text("BottomText")
                        }
                    }
                }
            }
            val top = onNodeWithText("TopText").fetchSemanticsNode().boundsInRoot
            val bottom = onNodeWithText("BottomText").fetchSemanticsNode().boundsInRoot
            onNodeWithText("TopText").performMouseInput {
                moveTo(Offset(2f, top.height / 2))
                press()
                for (i in 1..10) {
                    moveTo(
                        Offset(
                            2f + (bottom.right - top.left - 3f) * i / 10f,
                            top.height / 2 + (bottom.center.y - top.center.y) * i / 10f,
                        ),
                    )
                }
                release()
            }
            val modifier = if (isMac) Key.MetaLeft else Key.CtrlLeft
            onNodeWithText("BottomText").performKeyInput {
                keyDown(modifier)
                keyDown(Key.C)
                keyUp(Key.C)
                keyUp(modifier)
            }
            waitForIdle()
        }
        return clipboard.copied
    }

    @Test
    fun aFilledButtonLabelIsNotCopiedByADragAcrossIt() {
        assertEquals("TopText\nBottomText", dragSelectAndCopy(FormInputButtonStyle.FILLED))
    }

    @Test
    fun anOutlinedButtonLabelIsNotCopiedByADragAcrossIt() {
        assertEquals("TopText\nBottomText", dragSelectAndCopy(FormInputButtonStyle.OUTLINED))
    }

    @Test
    fun aTextButtonLabelIsNotCopiedByADragAcrossIt() {
        assertEquals("TopText\nBottomText", dragSelectAndCopy(FormInputButtonStyle.TEXT))
    }
}
