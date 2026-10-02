package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.runtime.Composable

/**
 * Shows the platform's file chooser while [show] is true. [extensions] limits what can be picked; [onFileSelected] receives the
 * path, name and size of the file, or an error message when nothing could be picked. The caller sets [show] back to false.
 */
@Composable
public expect fun FilePicker(
    show: Boolean,
    extensions: List<String> = listOf("pdf", "jpg", "png"),
    onFileSelected: (path: String?, name: String?, size: Long?, error: String?) -> Unit,
)
