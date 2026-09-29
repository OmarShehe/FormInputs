package com.omarshehe.forminput.compose.ui.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
actual fun FilePicker(
    show: Boolean,
    extensions: List<String>,
    onFileSelected: (path: String?, name: String?, size: Long?, error: String?) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            val fileInfo = getFileInfo(context, uri)
            onFileSelected(uri.toString(), fileInfo.name, fileInfo.size, null)
        } else {
            onFileSelected(null, null, null, null)
        }
    }

    var wasShown by remember { mutableStateOf(false) }

    LaunchedEffect(show) {
        if (show && !wasShown) {
            val mimeTypes = extensions.mapNotNull { ext ->
                when (ext.lowercase()) {
                    "pdf" -> "application/pdf"
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    else -> null
                }
            }.toTypedArray()

            if (mimeTypes.isNotEmpty()) {
                launcher.launch(mimeTypes)
            } else {
                launcher.launch(arrayOf("image/*", "application/pdf"))
            }
            wasShown = true
        } else if (!show) {
            wasShown = false
        }
    }
}

private data class FileInfo(val name: String?, val size: Long?)

private fun getFileInfo(
    context: Context,
    uri: Uri,
): FileInfo {
    var name: String? = null
    var size: Long? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (cursor.moveToFirst()) {
            name = cursor.getString(nameIndex)
            size = cursor.getLong(sizeIndex)
        }
    }
    return FileInfo(name, size)
}
