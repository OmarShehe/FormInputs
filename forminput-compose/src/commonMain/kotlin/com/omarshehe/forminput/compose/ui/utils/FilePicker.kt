package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.runtime.Composable

@Composable
expect fun FilePicker(
    show: Boolean,
    extensions: List<String> = listOf("pdf", "jpg", "png"),
    onFileSelected: (path: String?, name: String?, size: Long?, error: String?) -> Unit,
)
