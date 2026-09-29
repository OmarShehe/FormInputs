package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity

/**
 * Material's OutlinedTextField keeps half a floated label's height free above its border, so its layout is taller than the
 * drawn box and fields built on it sit further apart than [com.omarshehe.forminput.compose.ui.FormInputTextField]. This moves the
 * field up into that space and gives the space back, so both kinds of field take the same room. Does nothing without a label.
 */
@Composable
fun Modifier.trimOutlinedLabelSpace(hasLabel: Boolean): Modifier {
    if (!hasLabel) return this
    val trim = with(LocalDensity.current) { (MaterialTheme.typography.bodySmall.lineHeight / 2).roundToPx() }
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, (placeable.height - trim).coerceAtLeast(0)) { placeable.place(0, -trim) }
    }
}
