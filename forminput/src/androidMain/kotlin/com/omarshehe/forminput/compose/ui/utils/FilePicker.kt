package com.omarshehe.forminput.compose.ui.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
public actual fun FilePicker(
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
            // Android's own table knows every common extension (docx, xlsx, gif, webp, ...), not just a hand-picked few.
            val mimeTypes = extensions
                .mapNotNull { ext -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.trimStart('.').lowercase()) }
                .distinct()
                .toTypedArray()

            if (mimeTypes.isNotEmpty()) {
                launcher.launch(mimeTypes)
            } else {
                // Nothing was asked for, or Android knows none of it: offer every file rather than a picker that cannot pick the type.
                launcher.launch(arrayOf("*/*"))
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
            name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
            // A provider may leave the size NULL; getLong would turn that into 0 and let the file past a size limit.
            size = if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) cursor.getLong(sizeIndex) else null
        }
    }
    return FileInfo(name, size)
}
