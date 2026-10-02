package com.omarshehe.forminput.compose.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.omarshehe.forminput.compose.resources.Res
import com.omarshehe.forminput.compose.resources.select_file
import com.omarshehe.forminput.compose.ui.LocalFormInputDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import java.awt.FileDialog
import java.awt.Frame
import java.awt.KeyboardFocusManager
import java.io.File

@Composable
public actual fun FilePicker(
    show: Boolean,
    extensions: List<String>,
    onFileSelected: (path: String?, name: String?, size: Long?, error: String?) -> Unit,
) {
    val titleOverride = LocalFormInputDefaults.current.strings.selectFile
    LaunchedEffect(show) {
        if (show) {
            val title = titleOverride ?: getString(Res.string.select_file)
            var selectedFile: File? = null

            withContext(Dispatchers.IO) {
                // Resolve the active Compose window as the FileDialog owner so macOS Metal
                // has a valid parent surface. Passing null causes OutOfMemoryError (width=0, height=0).
                val owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow as? Frame
                val dialog = FileDialog(owner, title, FileDialog.LOAD)
                if (extensions.isNotEmpty()) {
                    dialog.setFilenameFilter { _, name ->
                        extensions.any { ext -> name.lowercase().endsWith(".$ext") }
                    }
                }
                dialog.isVisible = true
                val f = dialog.file
                val d = dialog.directory
                if (f != null && d != null) selectedFile = File(d, f)
            }

            if (selectedFile != null) {
                val f = selectedFile
                onFileSelected(f.absolutePath, f.name, f.length(), null)
            } else {
                onFileSelected(null, null, null, null)
            }
        }
    }
}
