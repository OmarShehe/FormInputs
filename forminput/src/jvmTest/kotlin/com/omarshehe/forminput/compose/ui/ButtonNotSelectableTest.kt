package com.omarshehe.forminput.compose.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import kotlin.test.Test
import kotlin.test.assertEquals

/** A drag-selection across a button copies the text around it but not the button's label. */
@OptIn(ExperimentalTestApi::class)
class ButtonNotSelectableTest {
    private val isMac = System.getProperty("os.name").orEmpty().startsWith("Mac", ignoreCase = true)

    private fun clipboard(): String =
        runCatching { Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) as String }.getOrDefault("")

    private fun dragSelectAndCopy(style: FormInputButtonStyle): String {
        var copied = ""
        runComposeUiTest {
            setContent {
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
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(""), null)
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
            copied = clipboard()
        }
        return copied
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
