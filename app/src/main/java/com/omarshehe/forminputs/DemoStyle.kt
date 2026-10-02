package com.omarshehe.forminputs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.omarshehe.forminput.compose.ui.model.FormInputFieldStyle
import com.omarshehe.forminput.compose.ui.utils.formInputModifier
import com.omarshehe.forminput.compose.ui.utils.formInputShape

internal val ScreenPadding = 16.dp
internal val SectionGap = 20.dp
internal val FieldGap = 8.dp
internal val SwitchGap = 12.dp

/**
 * How the demo draws its fields. [style] and [shape] go into the `FormInputTheme` around the screen, so no field repeats them;
 * [fieldModifier] is the one thing each field still takes, because the classic look pads fields with `formInputModifier`.
 */
internal class DemoLook(
    val classic: Boolean,
    val fieldModifier: Modifier,
    val shape: Shape,
    val style: FormInputFieldStyle,
)

/** The old Compose look (filled fields, formInputModifier, square bottom corners) or the default outlined look. */
@Composable
internal fun demoLook(classic: Boolean): DemoLook =
    if (classic) {
        // The screen already pads its content, so the modifier adds no padding of its own here.
        DemoLook(true, Modifier.formInputModifier(padding = PaddingValues(0.dp)), formInputShape(), FormInputFieldStyle.FILLED)
    } else {
        DemoLook(false, Modifier, OutlinedTextFieldDefaults.shape, FormInputFieldStyle.OUTLINED)
    }
