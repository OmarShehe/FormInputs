package com.omarshehe.forminput.compose.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.painter.Painter

@Composable
internal fun FormInputIcon(
    modifier: Modifier = Modifier,
    icon: Any,
    tint: Color = LocalContentColor.current,
    description: Any? = null
) {
    when (icon) {
        is ImageVector -> {
            Icon(
                modifier = modifier,
                imageVector = icon,
                tint = tint,
                contentDescription = description?.asText()
            )
        }

        is Painter -> {
            Icon(
                modifier = modifier,
                painter = icon,
                tint = tint,
                contentDescription = description?.asText()
            )
        }
    }
}
