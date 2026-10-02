package com.omarshehe.forminput.compose.ui.utils.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
public expect fun rememberImageBitmap(
    path: String,
    maxWidth: Int? = null,
    maxHeight: Int? = null,
): ImageBitmap?
