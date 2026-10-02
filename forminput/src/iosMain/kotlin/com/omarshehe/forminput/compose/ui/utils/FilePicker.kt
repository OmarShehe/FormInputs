package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.error_file_picker_unsupported
import com.omarshehe.forminput.compose.ui.LocalFormInputDefaults
import org.jetbrains.compose.resources.getString

/** iOS has no document picker here yet: opening it reports an error through the callback. */
@Composable
public actual fun FilePicker(
    show: Boolean,
    extensions: List<String>,
    onFileSelected: (path: String?, name: String?, size: Long?, error: String?) -> Unit,
) {
    val unsupported = LocalFormInputDefaults.current.strings.filePickerUnsupported
    LaunchedEffect(show) {
        if (show) onFileSelected(null, null, null, unsupported ?: getString(Res.string.error_file_picker_unsupported))
    }
}
